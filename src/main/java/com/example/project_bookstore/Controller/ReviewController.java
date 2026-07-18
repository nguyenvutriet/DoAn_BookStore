package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Entity.Customers;
import com.example.project_bookstore.Entity.OrderDetail;
import com.example.project_bookstore.Entity.Orders;
import com.example.project_bookstore.Entity.Review;
import com.example.project_bookstore.Entity.Users;
import com.example.project_bookstore.Service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private ReviewSummaryService reviewSummaryService;

    @Autowired
    private BooksService booksService;

    // Lấy customer đang đăng nhập
//    private Customers getLoggedInCustomer() {
////        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
////
////        if (auth == null || !auth.isAuthenticated()
////                || auth.getPrincipal().equals("anonymousUser")) {
////            return null;
////        }
//
//        String username = userDetails.getUsername();
//        Users user = usersService.getUserByUserName(username);
//
//        return (user == null) ? null : user.getCustomer();
//    }


    /** ============================
     * 1. TRANG CHỌN SÁCH ĐỂ ĐÁNH GIÁ
     * ============================ */
    @GetMapping("/order/{orderId}")
    public String reviewBooksInOrder(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("orderId") String orderId, Model model) {

        String username =  userDetails.getUsername();
        Users us = usersService.getUserByUserName(username);

        Customers customer = us.getCustomer();
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
    public String reviewSingleBook(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("orderId") String orderId,
                                   @PathVariable("bookId") String bookId,
                                   Model model) {

        String username =  userDetails.getUsername();
        Users us = usersService.getUserByUserName(username);

        Customers customer = us.getCustomer();
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
    public String saveReview(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute("reviewRequest") Review form,
                             @RequestParam("bookId") String bookId,
                             @RequestParam("orderId") String orderId) {

        String username =  userDetails.getUsername();
        Users us = usersService.getUserByUserName(username);

        Customers customer = us.getCustomer();
        if (customer == null) return "redirect:/login";

        Review existing = reviewService.getReviewByCustomerAndBook(
                customer.getCustomerId(),
                bookId
        );

        String encodedBookId = URLEncoder.encode(bookId, StandardCharsets.UTF_8);

        if (existing == null) {
            // Tạo mới
            reviewService.createReview(bookId, form.getRating(), form.getComment(), customer);

            reviewSummaryService.updateBookSummary(bookId);

            return "redirect:/review/order/" + orderId + "?success=" + encodedBookId;

        } else {
            // Update review
            existing.setRating(form.getRating());
            existing.setComment(form.getComment());
            existing.setCreationDate(new Date());
            reviewService.update(existing);

            reviewSummaryService.updateBookSummary(bookId);

            return "redirect:/review/order/" + orderId + "?updated=" + encodedBookId;
        }
    }
    // ============================
    // 1. Xem chi tiết đơn hàng
    // ============================
    @GetMapping("/detail/{orderId}")
    public String viewOrderDetail(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String orderId,
                                  Model model) {

        String username =  userDetails.getUsername();
        Users us = usersService.getUserByUserName(username);

        Customers customer = us.getCustomer();

        if (customer == null) {
            return "redirect:/login";
        }

        Orders order = orderService.getOrderById(orderId);

        // Kiểm tra: đơn có tồn tại và có phải của user
        if (order == null ||
                !order.getCustomer().getCustomerId().equals(customer.getCustomerId())) {

            return "redirect:/user/myOrder";
        }

        // Tự động hủy nếu đơn VNPay đã quá 5 phút mà chưa thanh toán lại
        orderService.expireIfNeeded(order);

        // Đẩy order sang view
        model.addAttribute("order", order);

        // Cho template biết có hiện nút "Thanh toán lại" không, và còn bao nhiêu giây
        model.addAttribute("canRetry", orderService.canRetryPayment(order));
        model.addAttribute("remainingSeconds", orderService.getRemainingSeconds(order));

        return "review/order_detail";
    }
}
