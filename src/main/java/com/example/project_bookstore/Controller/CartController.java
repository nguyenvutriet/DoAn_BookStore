package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Entity.*;
import com.example.project_bookstore.Repository.IBooksRepository;
import com.example.project_bookstore.Repository.ICartDetailRepository;
import com.example.project_bookstore.Repository.ICartRepository;
import com.example.project_bookstore.Repository.IUsersRepository;
import com.example.project_bookstore.Service.CartDetailService;
import com.example.project_bookstore.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartDetailService cartDetailService;

    @Autowired
    private IUsersRepository usersRepository;

    @Autowired
    private IBooksRepository booksRepository;

    @Autowired
    private ICartDetailRepository cartDetailRepo;

    @Autowired
    private ICartRepository cartRepo;


    // ========================= HIỂN THỊ GIỎ HÀNG =============================
    @GetMapping("/gio_hang")
    public String showCart(Model model, @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();

        Users user = usersRepository.findById(username).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        String customerId = user.getCustomer().getCustomerId();

        Cart cart = cartService.getCartByCustomer(customerId);
        if (cart == null) {
            model.addAttribute("items", new ArrayList<>());
            model.addAttribute("total", 0);
            return "cart_form";
        }

        List<CartDetail> details = cartDetailService.getDetailsByCart(cart);
        List<ViewCartItem> items = new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;

        for (CartDetail d : details) {

            Books book = booksRepository.findById(d.getCartDetailId().getBookId())
                    .orElse(null);

            if (book == null) continue;

            ViewCartItem item = new ViewCartItem(
                    d.getCartDetailId().getCartId(),
                    book.getBookId(),
                    book.getTitle(),
                    book.getPicture(),
                    d.getUnitPrice().doubleValue(),
                    d.getQuantity()
            );

            items.add(item);

            total = total.add(
                    d.getUnitPrice().multiply(BigDecimal.valueOf(d.getQuantity()))
            );
        }

        model.addAttribute("items", items);
        model.addAttribute("total", total);

        return "cart_form";
    }


    // ========================= CẬP NHẬT SỐ LƯỢNG =============================
    @PostMapping("/gio_hang/update")
    @ResponseBody
    public Map<String, Object> updateQuantity(
            @RequestParam String cartId,
            @RequestParam String bookId,
            @RequestParam int quantity) {

        CartDetailId id = new CartDetailId(cartId, bookId);
        CartDetail detail = cartDetailRepo.findById(id).orElse(null);

        if (detail != null) {
            detail.setQuantity(quantity);
            cartDetailRepo.save(detail);
        }

        List<CartDetail> list = cartDetailRepo.findByCartDetailId_CartId(cartId);

        BigDecimal total = list.stream()
                .map(cd -> cd.getUnitPrice().multiply(BigDecimal.valueOf(cd.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Cart cart = cartRepo.findById(cartId).orElse(null);
        if (cart != null) {
            cart.setTotalAmount(total);
            cartRepo.save(cart);
        }

        BigDecimal subtotal = detail.getUnitPrice().multiply(BigDecimal.valueOf(quantity));

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("subtotal", subtotal);
        response.put("total", total);

        return response;
    }


    // ========================= XÓA 1 SẢN PHẨM =============================
    @PostMapping("/cart/delete")
    @ResponseBody
    public Map<String, Object> deleteItem(@RequestBody Map<String, String> data) {

        String cartId = data.get("cartId");
        String bookId = data.get("bookId");

        CartDetailId id = new CartDetailId(cartId, bookId);
        cartDetailRepo.deleteById(id);

        List<CartDetail> list = cartDetailRepo.findByCartDetailId_CartId(cartId);

        BigDecimal total = list.stream()
                .map(cd -> cd.getUnitPrice().multiply(BigDecimal.valueOf(cd.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Cart cart = cartRepo.findById(cartId).orElse(null);
        if (cart != null) {
            cart.setTotalAmount(total);
            cartRepo.save(cart);
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("total", total);

        return resp;
    }
}
