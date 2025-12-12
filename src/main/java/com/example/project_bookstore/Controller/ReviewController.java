package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Entity.Customers;
import com.example.project_bookstore.Entity.OrderDetail;
import com.example.project_bookstore.Entity.Orders;
import com.example.project_bookstore.Entity.Review;
import com.example.project_bookstore.Entity.Users;
import com.example.project_bookstore.Service.BooksService;
import com.example.project_bookstore.Service.CustomersService;
import com.example.project_bookstore.Service.OrdersService;
import com.example.project_bookstore.Service.ReviewService;
import com.example.project_bookstore.Service.UsersService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private OrdersService orderService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private BooksService booksService;


    // Lấy customer đang đăng nhập
    private Customers getLoggedInCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        String username = auth.getName();
        Users user = usersService.getUserByUserName(username);

        return (user == null) ? null : user.getCustomer();
    }


    /** ============================
     * 1. TRANG CHỌN SÁCH ĐỂ ĐÁNH GIÁ
     * ============================ */
    @GetMapping("/order/{orderId}")
    public String reviewBooksInOrder(@PathVariable String orderId, Model model) {

        Customers customer = getLoggedInCustomer();
        if (customer == null) return "redirect:/login";

        Orders order = orderService.getOrderById(orderId);

        // Không cho xem đơn của người khác
        if (order == null ||
                !order.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            return "redirect:/user/myOrder";
        }

        // Map kiểm tra đã đánh giá chưa
        Map<String, Boolean> ratedMap = new HashMap<>();
        for (OrderDetail item : order.getOrderDetail_Order()) {
            boolean exists = reviewService.customerHasReviewedBook(
                    customer.getCustomerId(),
                    item.getBook().getBookId()
            );
            ratedMap.put(item.getBook().getBookId(), exists);
        }

        model.addAttribute("order", order);
        model.addAttribute("ratedMap", ratedMap);

        return "review/review_order_books";
    }


    /** ============================
     * 2. FORM ĐÁNH GIÁ / CHỈNH SỬA REVIEW
     * ============================ */
    @GetMapping("/add/{orderId}/{bookId}")
    public String reviewSingleBook(@PathVariable String orderId,
                                   @PathVariable String bookId,
                                   Model model) {

        Customers customer = getLoggedInCustomer();
        if (customer == null) return "redirect:/login";

        // Kiểm tra đã có review chưa
        Review oldReview = reviewService.getReviewByCustomerAndBook(
                customer.getCustomerId(),
                bookId
        );

        model.addAttribute("book", booksService.getBookById(bookId));
        model.addAttribute("bookId", bookId);
        model.addAttribute("orderId", orderId);

        if (oldReview != null) {
            // Đã có review → vào chế độ EDIT
            model.addAttribute("reviewRequest", oldReview);
            model.addAttribute("isUpdate", true);
        } else {
            // Chưa có → tạo mới
            model.addAttribute("reviewRequest", new Review());
            model.addAttribute("isUpdate", false);
        }

        return "review/review_form";
    }


    /** ============================
     * 3. LƯU ĐÁNH GIÁ (MỚI HOẶC UPDATE)
     * ============================ */
    @PostMapping("/save")
    public String saveReview(@ModelAttribute("reviewRequest") Review form,
                             @RequestParam String bookId,
                             @RequestParam String orderId) {

        Customers customer = getLoggedInCustomer();
        if (customer == null) return "redirect:/login";

        Review existing = reviewService.getReviewByCustomerAndBook(
                customer.getCustomerId(),
                bookId
        );

        String encodedBookId = URLEncoder.encode(bookId, StandardCharsets.UTF_8);

        if (existing == null) {
            // Tạo mới
            reviewService.createReview(bookId, form.getRating(), form.getComment(), customer);

            return "redirect:/review/order/" + orderId + "?success=" + encodedBookId;

        } else {
            // Update review
            existing.setRating(form.getRating());
            existing.setComment(form.getComment());
            existing.setCreationDate(new Date());
            reviewService.update(existing);

            return "redirect:/review/order/" + orderId + "?updated=" + encodedBookId;
        }
    }
//////////////////////////////////////////////////////////////
    // ============================
    // 1. Xem chi tiết đơn hàng
    // ============================
    @GetMapping("/detail/{orderId}")
    public String viewOrderDetail(@PathVariable String orderId,
                                  Model model) {

        Customers customer = getLoggedInCustomer();

        if (customer == null) {
            return "redirect:/login";
        }

        Orders order = orderService.getOrderById(orderId);

        // Kiểm tra: đơn có tồn tại và có phải của user
        if (order == null ||
                !order.getCustomer().getCustomerId().equals(customer.getCustomerId())) {

            return "redirect:/user/myOrder";
        }

        // Đẩy order sang view
        model.addAttribute("order", order);

        return "review/order_detail";   // file HTML bạn tạo: order_detail.html
    }
}
