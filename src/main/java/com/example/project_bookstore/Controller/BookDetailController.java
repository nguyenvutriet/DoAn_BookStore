package com.example.project_bookstore.Controller;


import com.example.project_bookstore.Entity.Books;
import com.example.project_bookstore.Entity.Customers;
import com.example.project_bookstore.Entity.Users;
import com.example.project_bookstore.Service.ReviewService;
import com.example.project_bookstore.Service.UsersService;
import com.example.project_bookstore.Repository.IBooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BookDetailController {
    @Autowired
    private IBooksRepository booksRepository;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UsersService usersService;

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
}
