package com.example.project_bookstore.Controller;


import com.example.project_bookstore.Entity.Books;
import com.example.project_bookstore.Entity.Customers;
import com.example.project_bookstore.Entity.Users;
import com.example.project_bookstore.Service.ReviewService;
import com.example.project_bookstore.Service.UsersService;
import com.example.project_bookstore.Service.BooksService;
import com.example.project_bookstore.Repository.IBooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BookDetailController {
    @Autowired
    private IBooksRepository booksRepository;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private BooksService booksService;

    // ====== POST: Nhận form đánh giá ngay trên trang chi tiết ======
    @PostMapping("/home/books/{id}/review")
    public String submitReview(@PathVariable("id") String bookId,
                               @RequestParam("rating") int rating,
                               @RequestParam(value = "comment", required = false) String comment,
                               RedirectAttributes redirectAttributes) {

        // Lấy user đang đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Users user = usersService.getUserByUserName(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy user: " + username);
        }

        Customers customer = user.getCustomer();
        if (customer == null) {
            throw new RuntimeException("User này chưa gắn Customers, không thể đánh giá.");
        }

        reviewService.createReview(bookId, rating, comment, customer);

        redirectAttributes.addFlashAttribute("message", "Cảm ơn bạn đã đánh giá sách!");

        // Redirect lại đúng trang chi tiết sách (không có bước /rating, back là ra ngoài luôn)
        return "redirect:/home/books/" + bookId;
    }
    @GetMapping("/home/books/{id}/review")
    public String reviewBook(@PathVariable("id") String bookId,
                               RedirectAttributes redirectAttributes) {

        // Lấy user đang đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Users user = usersService.getUserByUserName(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy user: " + username);
        }

        Customers customer = user.getCustomer();
        if (customer == null) {
            throw new RuntimeException("User này chưa gắn Customers, không thể đánh giá.");
        }

        // Redirect lại đúng trang chi tiết sách (không có bước /rating, back là ra ngoài luôn)
        return "redirect:/home/books/" + bookId;
    }

    @PostMapping("/checkout/fast-buy")
    public String fastBuy(@RequestParam String bookId,
                          @RequestParam int quantity,
                          Model model,
                          Principal principal) {

        Users user = usersService.getUserByUserName(principal.getName());
        Customers customer = user.getCustomer();

        Books book = booksService.getBookById(bookId);

        // BigDecimal price = book.getPrice();
        BigDecimal subtotal = book.getPrice().multiply(BigDecimal.valueOf(quantity));
        BigDecimal total = subtotal;

        // Không dùng DTO → dùng Map
        Map<String, Object> item = new HashMap<>();
        item.put("book", book);
        item.put("quantity", quantity);
        item.put("unitPrice", book.getPrice());
        item.put("total", subtotal);

        model.addAttribute("fullname", customer.getFullName());
        model.addAttribute("email", customer.getEmail());
        model.addAttribute("phone", customer.getPhone());
        model.addAttribute("address", customer.getAddress());

        // Truyền list items nhưng không cần class
        model.addAttribute("items", List.of(item));

        model.addAttribute("subtotal", subtotal);
        model.addAttribute("total", total);

        return "checkout";
    }
}
