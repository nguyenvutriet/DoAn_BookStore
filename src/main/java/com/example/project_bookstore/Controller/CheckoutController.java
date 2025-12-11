package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Entity.*;
import com.example.project_bookstore.Repository.*;
import com.example.project_bookstore.Service.VNPayService;
import com.example.project_bookstore.dto.PaymentDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;

@RequiredArgsConstructor

@Controller
public class CheckoutController {


    @Autowired
    private ICartDetailRepository cartDetailRepo;

    @Autowired
    private IOrdersRepository ordersRepo;

    @Autowired
    private IOrderDetailRepository orderDetailRepo;
    @Autowired
    private IUsersRepository usersRepository;
    @Autowired
    private ICartRepository cartRepo;
    @Autowired
    private IBooksRepository booksRepository;
    private final VNPayService vnPayService;


    @PostMapping("/checkout")
    public ResponseEntity<?> saveSelectedItems(@RequestBody List<CartSelectedItem> selectedItems,
                                               HttpSession session) {

        // Lưu danh sách vào session để qua trang checkout dùng
        session.setAttribute("checkout_items", selectedItems);

        return ResponseEntity.ok().body("OK");
    }

    @GetMapping("/checkout")
    public String checkoutPage(Model model, @AuthenticationPrincipal UserDetails userDetails, HttpSession session) {

        String username = userDetails.getUsername();

        Users user = usersRepository.findById(username).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Customers customer = user.getCustomer();

        model.addAttribute("fullname", customer.getFullName());
        model.addAttribute("email", customer.getEmail());
        model.addAttribute("phone", customer.getPhone());

        List<CartSelectedItem> items = (List<CartSelectedItem>) session.getAttribute("checkout_items");

        if (items == null) {
            return "redirect:/gio_hang";
        }

        // Lấy thông tin đầy đủ từ CartDetail
        List<CartDetail> cartDetails = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartSelectedItem s : items) {
            CartDetailId id = new CartDetailId(s.getCartId(), s.getBookId());
            CartDetail cd = cartDetailRepo.findById(id).orElse(null);

            if (cd != null) {
                cd.setQuantity(s.getQuantity());
                cd.getBook().getTitle();
                cartDetails.add(cd);
            }
            // ====== TÍNH SUBTOTAL ======
            BigDecimal price = cd.getBook().getPrice();
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(s.getQuantity()));

            subtotal = subtotal.add(lineTotal);

        }

        model.addAttribute("items", cartDetails);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("total", subtotal);

        return "checkout";  // form mua hàng
    }
    private String generateOrderId() {
        String lastId = ordersRepo.findLastOrderId();

        if (lastId == null) {
            return "OD0001"; // Khi chưa có đơn nào
        }

        // Tách phần chữ
        String prefix = lastId.replaceAll("[0-9]", "");

        // Tách số
        String numberPart = lastId.replaceAll("[^0-9]", "");

        int number = Integer.parseInt(numberPart);
        number++;

        // Padding giữ nguyên số chữ số
        String newNumber = String.format("%0" + numberPart.length() + "d", number);

        return prefix + newNumber;
    }


    @PostMapping("/checkout/submit")
    public String submitOrder(@ModelAttribute OrderForm form,
                              HttpSession session,
                              HttpServletRequest request,
                              @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        Users user = usersRepository.findById(username).orElse(null);
        if (user == null) return "redirect:/login";

        Customers customer = user.getCustomer();

        // ===== Tạo order =====
        Orders order = new Orders();
        order.setOrderId(generateOrderId());
        order.setStatus("Pending");
        order.setOrderDate(new Date());
        order.setPaymentMethod(form.getPaymentMethod());
        order.setAddress(form.getAddress());
        order.setCustomer(customer);

        BigDecimal total = BigDecimal.ZERO;
        List<OrderDetail> details = new ArrayList<>();

        // ===== Tính subtotal + tạo detail =====
        for (CartSelectedItem item : form.getItems()) {

            BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(subtotal);

            OrderdetailId id = new OrderdetailId(order.getOrderId(), item.getBookId());

            OrderDetail detail = new OrderDetail();
            detail.setOrderDetailId(id);
            detail.setOrder(order);
            detail.setBook(booksRepository.findById(item.getBookId()).orElse(null));
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getUnitPrice());

            details.add(detail);
        }

        // Set đầy đủ thông tin
        order.setTotalAmount(total);
        order.setOrderDetail_Order(details);

        // Lưu order
        ordersRepo.save(order);

        // Xóa cart
        Cart cart = cartRepo.findByCustomer(customer);
        if (cart != null) {
            for (CartSelectedItem item : form.getItems()) {
                cartDetailRepo.deleteByCartAndBook_BookId(cart, item.getBookId());
            }
        }

        // ===== XỬ LÝ PHƯƠNG THỨC THANH TOÁN =====

        // Nếu không phải VNPay -> COD => redirect success
        String pm = form.getPaymentMethod().toLowerCase();

        if (!pm.contains("vnpay")) {
            return "redirect:/checkout/success?orderId=" + order.getOrderId();
        }


        // Nếu là VNPay → tạo URL và redirect
        PaymentDTO pay = new PaymentDTO();
        pay.setAmount(total.longValue());
        pay.setOrderId(order.getOrderId());
        pay.setOrderInfo("Thanh toan don hang #" + order.getOrderId());

        try {
            String paymentUrl = vnPayService.createPaymentUrl(pay, request);
            return "redirect:" + paymentUrl;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            // Có thể redirect sang trang báo lỗi
            return "redirect:/checkout/failure";
        }


    }


    @GetMapping("/checkout/success")
    public String success(@RequestParam("orderId") String orderId, Model model) {

        Orders order = ordersRepo.findById(orderId).orElse(null);
        List<OrderDetail> details = orderDetailRepo.findByOrder_OrderId(orderId);

        model.addAttribute("order", order);
        model.addAttribute("details", details);


        return "success";
    }

    @GetMapping("/checkout/fallure")
    public String fallure(@RequestParam("orderId") String orderId, Model model) {

        Orders order = ordersRepo.findById(orderId).orElse(null);
        List<OrderDetail> details = orderDetailRepo.findByOrder_OrderId(orderId);

        model.addAttribute("order", order);
        model.addAttribute("details", details);


        return "fallure";
    }



}





