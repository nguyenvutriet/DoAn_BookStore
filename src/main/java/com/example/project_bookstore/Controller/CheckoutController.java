package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Entity.*;
import com.example.project_bookstore.Repository.*;
import com.example.project_bookstore.Service.*;
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
import org.thymeleaf.context.Context;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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
    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private OrdersService orService;

    @Autowired
    private FlashSaleService flashSaleService;

    @Autowired
    private OrderEmailService orderEmailService;

    private final OrdersService orderService;
    private final VNPayService vnPayService;
    private final EmailService emailService;

    @Autowired
    private PaymentService paymentService;


    @PostMapping("/checkout")
    public ResponseEntity<?> saveSelectedItems(
            @RequestBody List<CartSelectedItem> selectedItems,
            HttpSession session) {

        System.out.println("SAVE CHECKOUT");
        System.out.println(selectedItems);

        session.setAttribute("checkout_items", selectedItems);

        return ResponseEntity.ok("OK");
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
            return "redirect:/gio_hang?error=item_null";
        }

        List<CartDetail> cartDetails = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        Map<String, BigDecimal> effectivePriceMap = new HashMap<>();

        for (CartSelectedItem s : items) {
            CartDetailId id = new CartDetailId(s.getCartId(), s.getBookId());
            CartDetail cd = cartDetailRepo.findById(id).orElse(null);

            if (cd == null) {
                continue;
            }

            cd.setQuantity(s.getQuantity());
            cartDetails.add(cd);

            BigDecimal originalPrice = cd.getBook().getPrice();
            BigDecimal effectivePrice = flashSaleService.getActiveSaleForBook(s.getBookId())
                    .map(FlashSaleDetail::getSalePrice)
                    .orElse(originalPrice);

            effectivePriceMap.put(s.getBookId(), effectivePrice);

            BigDecimal lineTotal = effectivePrice.multiply(BigDecimal.valueOf(s.getQuantity()));
            subtotal = subtotal.add(lineTotal);
        }

        model.addAttribute("items", cartDetails);
        model.addAttribute("effectivePriceMap", effectivePriceMap);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("total", subtotal);

        return "checkout";
    }

    private String generateOrderId() {
        return orService.generateId();
    }


    @PostMapping("/checkout/submit")
    public String submitOrder(
            @ModelAttribute OrderForm form,
            @RequestParam("g-recaptcha-response")
            String captchaToken,
            @RequestParam(required = false)
            String fingerprint,
            HttpSession session,
            HttpServletRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        Users user = usersRepository.findById(username).orElse(null);
        if (user == null) return "redirect:/login";

        if (!captchaService.verify(captchaToken)) {
            return "redirect:/checkout?captchaError";
        }

        System.out.println("Fingerprint = " + fingerprint);

        Customers customer = user.getCustomer();

        // ===== Tạo order =====
        Orders order = new Orders();
        order.setOrderId(generateOrderId());
        order.setStatus("Pending");

        ZonedDateTime vietnamTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        Date vietnamDate = Date.from(vietnamTime.toInstant());

        order.setOrderDate(vietnamDate);
        order.setPaymentMethod(form.getPaymentMethod());
        order.setAddress(form.getAddress());
        order.setCustomer(customer);
        order.setDeviceFingerprint(fingerprint);
        boolean suspicious = false;
        String reason = "";

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date startOfDay = cal.getTime();

        long orderCountToday =
                ordersRepo.countOrdersToday(
                        customer.getCustomerId(),
                        startOfDay);

        long fingerprintOrderCount = 0;

        if (fingerprint != null && !fingerprint.isBlank()) {
            fingerprintOrderCount =
                    ordersRepo.countByDeviceFingerprint(
                            fingerprint);
        }

        if (fingerprintOrderCount >= 5) {
            suspicious = true;
            reason += "Thiết bị đã tạo trên 5 đơn hàng; ";
        }

        long customerCount = 0;

        if (fingerprint != null && !fingerprint.isBlank()) {
            customerCount =
                    ordersRepo
                            .countDistinctCustomersByFingerprint(
                                    fingerprint);
        }

        if (customerCount >= 3) {
            suspicious = true;
            reason += "Có từ 3 tài khoản trở lên sử dụng cùng thiết bị; ";
        }

        if (orderCountToday >= 5) {
            suspicious = true;
            reason += "Khách đã đặt trên 5 đơn trong ngày; ";
        }

        List<OrderDetail> details = new ArrayList<>();

        // ===== TÍNH LẠI GIÁ THẬT Ở SERVER (KHÔNG TIN unitPrice TỪ CLIENT) =====
        BigDecimal recalculatedSubtotal = BigDecimal.ZERO;

        for (CartSelectedItem item : form.getItems()) {

            Books book = booksRepository.findById(item.getBookId()).orElse(null);
            if (book == null) continue;

            BigDecimal realPrice = flashSaleService.getActiveSaleForBook(item.getBookId())
                    .map(FlashSaleDetail::getSalePrice)
                    .orElse(book.getPrice());

            OrderdetailId id = new OrderdetailId(order.getOrderId(), item.getBookId());

            OrderDetail detail = new OrderDetail();
            detail.setOrderDetailId(id);
            detail.setOrder(order);
            detail.setBook(book);
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(realPrice);

            details.add(detail);

            recalculatedSubtotal = recalculatedSubtotal.add(
                    realPrice.multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }

        BigDecimal shippingFee = form.getShippingFee() != null ? form.getShippingFee() : BigDecimal.ZERO;
        BigDecimal finalTotal = recalculatedSubtotal.add(shippingFee);
        if (finalTotal.compareTo(BigDecimal.valueOf(5000000)) > 0) {
            suspicious = true;
            reason += "Đơn hàng có giá trị lớn (> 5 triệu); ";
        }

        int totalQuantity = 0;

        for (CartSelectedItem item : form.getItems()) {
            totalQuantity += item.getQuantity();
        }

        if (totalQuantity >= 20) {
            suspicious = true;
            reason += "Số lượng sách trong đơn quá lớn (>=20); ";
        }

        order.setFraudFlag(suspicious);
        order.setFraudReason(reason);

        order.setTotalAmount(finalTotal);
        order.setOrderDetail_Order(details);

        System.out.println(order.getOrderId() + order.getOrderDate() + order.getAddress());

        // Lưu order
        try {
            orderService.placeOrder(order, details);
        } catch (Exception e) {
            if (e.getMessage() != null &&
                    e.getMessage().contains("Số lượng đặt vượt quá số lượng tồn kho")) {
                return "redirect:/gio_hang?error=out_of_stock";
            }
            return "redirect:/gio_hang?error=order_failed";
        }

        // ===== XÁC ĐỊNH PHƯƠNG THỨC THANH TOÁN =====
        String pm = form.getPaymentMethod().toLowerCase();
        boolean isVnpay = pm.contains("vnpay");

        // ===== CHỈ GỬI EMAIL NGAY NẾU LÀ COD =====
        // VNPay: KHÔNG gửi mail ở đây, chỉ gửi sau khi VNPayReturnController xác nhận thanh toán thành công
        if (!isVnpay) {
            orderEmailService.sendOrderConfirmationEmail(order, customer);
        }

        // Xóa cart
        Cart cart = cartRepo.findByCustomer(customer);
        if (cart != null) {
            for (CartSelectedItem item : form.getItems()) {
                cartDetailRepo.deleteByCartAndBook_BookId(cart, item.getBookId());
            }
        }

        if (!isVnpay) {
            return "redirect:/checkout/success?orderId=" + order.getOrderId();
        }

        // ===== VNPAY: chỉ tạo URL thanh toán, không gửi mail =====
        PaymentDTO pay = new PaymentDTO();
        pay.setAmount(finalTotal.longValue());
        pay.setOrderId(order.getOrderId());
        pay.setOrderInfo("Thanh toan don hang #" + order.getOrderId());

        try {
            String paymentUrl = vnPayService.createPaymentUrl(pay, request);
            return "redirect:" + paymentUrl;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "redirect:/checkout/failure";
        }
    }


    @GetMapping("/checkout/success")
    public String success(@RequestParam("orderId") String orderId, Model model) {

        Orders order = ordersRepo.findById(orderId).orElse(null);
        List<OrderDetail> details = orderDetailRepo.findByOrder_OrderId(orderId);

        if (order == null || details == null) {
            return "error";
        }

        BigDecimal subtotalBD = BigDecimal.ZERO;

        for (OrderDetail d : details) {
            BigDecimal quantityBD = BigDecimal.valueOf(d.getQuantity());
            BigDecimal lineTotal = d.getUnitPrice().multiply(quantityBD);
            subtotalBD = subtotalBD.add(lineTotal);
        }

        BigDecimal shippingFeeBD = order.getTotalAmount().subtract(subtotalBD);

        model.addAttribute("order", order);
        model.addAttribute("details", details);

        model.addAttribute("subtotal", subtotalBD.longValue());
        model.addAttribute("shippingFee", shippingFeeBD.longValue());
        model.addAttribute("totalAmount", order.getTotalAmount().longValue());

        return "success";
    }





    // ===== Thanh toán lại đơn VNPay còn trong hạn 5 phút =====
    @PostMapping("/checkout/retry/{orderId}")
    public String retryPayment(@PathVariable String orderId, HttpServletRequest request) {

        Orders order = ordersRepo.findById(orderId).orElse(null);
        if (order == null) return "redirect:/user/myOrder";

        orService.expireIfNeeded(order);

        if (!orService.canRetryPayment(order)) {
            return "redirect:/checkout/fallure?orderId=" + orderId + "&error=expired";
        }

        PaymentDTO pay = new PaymentDTO();
        pay.setAmount(order.getTotalAmount().longValue());
        pay.setOrderId(order.getOrderId());
        pay.setOrderInfo("Thanh toan lai don hang #" + order.getOrderId());

        try {
            String paymentUrl = vnPayService.createPaymentUrl(pay, request);
            return "redirect:" + paymentUrl;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "redirect:/checkout/fallure?orderId=" + orderId;
        }
    }

    @GetMapping("/checkout/fallure")
    public String fallure(@RequestParam("orderId") String orderId, Model model) {

        Orders order = ordersRepo.findById(orderId).orElse(null);
        List<OrderDetail> details = orderDetailRepo.findByOrder_OrderId(orderId);

        if (order == null || details == null) {
            return "error";
        }

        orService.expireIfNeeded(order); // đơn có thể vừa quá hạn khi load trang này

        BigDecimal subtotalBD = BigDecimal.ZERO;
        for (OrderDetail d : details) {
            BigDecimal quantityBD = BigDecimal.valueOf(d.getQuantity());
            subtotalBD = subtotalBD.add(d.getUnitPrice().multiply(quantityBD));
        }
        BigDecimal shippingFeeBD = order.getTotalAmount().subtract(subtotalBD);

        model.addAttribute("order", order);
        model.addAttribute("details", details);
        model.addAttribute("subtotal", subtotalBD.longValue());
        model.addAttribute("shippingFee", shippingFeeBD.longValue());
        model.addAttribute("totalAmount", order.getTotalAmount().longValue());

        // Mới: cho template biết còn được thanh toán lại không
        model.addAttribute("canRetry", orService.canRetryPayment(order));
        model.addAttribute("remainingSeconds", orService.getRemainingSeconds(order));

        return "fallure";
    }

}