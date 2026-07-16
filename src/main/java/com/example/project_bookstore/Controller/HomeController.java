package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Entity.Books;
import com.example.project_bookstore.Entity.FlashSaleDetail;
import com.example.project_bookstore.Entity.Review;
import com.example.project_bookstore.Service.BooksService;
import com.example.project_bookstore.Service.RecommendationService;
import com.example.project_bookstore.Service.ReviewService;
import com.example.project_bookstore.Service.FlashSaleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private BooksService booksService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private FlashSaleService flashSaleService;
    @Autowired
    private RecommendationService recommendationService;

//    @GetMapping
//    public String home(
//            @RequestParam(value = "categoryId", required = false) String categoryId,
//            Model model
//    ) {
//
//        List<Books> newBooks = booksService.getNewBooks(categoryId);
//        List<Books> bestSellingBooks = booksService.getBestSellingBooks(categoryId);
//        List<Books> favoriteBooks = booksService.getFavoriteBooks(categoryId);
//
//        Map<String, Double> avgRatings = new HashMap<>();
//        Map<String, Long> ratingCounts = new HashMap<>();
//
//        // GỘP 3 LIST vào 1 vòng lặp chung
//        List<Books> allBooks = new java.util.ArrayList<>();
//        allBooks.addAll(newBooks);
//        allBooks.addAll(bestSellingBooks);
//        allBooks.addAll(favoriteBooks);
//
//        for (Books b : allBooks) {
//            int rounded = reviewService.getAverageRatingRounded(b.getBookId());
//            avgRatings.put(b.getBookId(), (double) rounded);
//            ratingCounts.put(b.getBookId(), reviewService.getReviewCountForBook(b.getBookId()));
//        }
//
//        model.addAttribute("newBooks", newBooks);
//        model.addAttribute("bestSellingBooks", bestSellingBooks);
//        model.addAttribute("favoriteBooks", favoriteBooks);
//
//        model.addAttribute("avgRatings", avgRatings);
//        model.addAttribute("ratingCounts", ratingCounts);
//
//        model.addAttribute("selectedCategoryId", categoryId);
//
//        return "index";
//    }


    @GetMapping
    public String home(
            @RequestParam(value = "categoryId", required = false) String categoryId,

            // chỉ dùng cho "Tất cả"
            @RequestParam(value = "pageAll", defaultValue = "0") int pageAll,

            // giữ lại để không lỗi link cũ, nhưng sẽ không dùng
            @RequestParam(value = "pageNew",  defaultValue = "0") int pageNew,
            @RequestParam(value = "pageBest", defaultValue = "0") int pageBest,
            @RequestParam(value = "pageFav",  defaultValue = "0") int pageFav,

            @RequestParam(value = "size", defaultValue = "8") int size,
            Model model
    ) {
        // 3 section trên: luôn lấy trang 0 (không phân trang)
        Pageable pTop = PageRequest.of(0, size);

        Page<Books> newBooksPage = booksService.getNewBooksPage(categoryId, pTop);
        Page<Books> bestSellingBooksPage = booksService.getBestSellingBooksPage(categoryId, pTop);
        Page<Books> favoriteBooksPage = booksService.getFavoriteBooksPage(categoryId, pTop);

        // "Tất cả": có phân trang riêng (xếp theo ngày xuất bản)
        Pageable pAll = PageRequest.of(pageAll, size);
        Page<Books> allBooksPage = booksService.getNewBooksPage(categoryId, pAll);

        // Rating cho sách đang hiển thị (4 list)
        Map<String, Double> avgRatings = new HashMap<>();
        Map<String, Long> ratingCounts = new HashMap<>();

        List<Books> visible = new ArrayList<>();
        visible.addAll(newBooksPage.getContent());
        visible.addAll(bestSellingBooksPage.getContent());
        visible.addAll(favoriteBooksPage.getContent());
        visible.addAll(allBooksPage.getContent());

        for (Books b : visible) {
            int rounded = reviewService.getAverageRatingRounded(b.getBookId());
            avgRatings.put(b.getBookId(), (double) rounded);
            ratingCounts.put(b.getBookId(), reviewService.getReviewCountForBook(b.getBookId()));
        }

        model.addAttribute("selectedCategoryId", categoryId);

        // 3 section trên: chỉ dùng content, không cần nav
        model.addAttribute("newBooks", newBooksPage.getContent());
        model.addAttribute("bestSellingBooks", bestSellingBooksPage.getContent());
        model.addAttribute("favoriteBooks", favoriteBooksPage.getContent());

        // "Tất cả"
        model.addAttribute("allBooksPage", allBooksPage);
        model.addAttribute("allBooks", allBooksPage.getContent());
        model.addAttribute("pageAll", pageAll);

        model.addAttribute("avgRatings", avgRatings);
        model.addAttribute("ratingCounts", ratingCounts);

        // Flash sale map for visible books
        java.util.List<String> bookIds = visible.stream().map(Books::getBookId).toList();
        java.util.Map<String, com.example.project_bookstore.Entity.FlashSaleDetail> flashSaleMap = flashSaleService.getActiveSaleMapForBooks(bookIds);
        model.addAttribute("flashSaleMap", flashSaleMap);
        model.addAttribute("currentFlashSale", flashSaleService.getCurrentActive().orElse(null));

        // giữ size để link phân trang "Tất cả" dùng
        model.addAttribute("size", size);

        // ==== Dữ liệu cho bảng tin chạy (flash sale ticker) ====
        List<Map<String, Object>> flashSaleTicker = new ArrayList<>();
        Map<String, Boolean> seenTicker = new HashMap<>();

        for (Books b : visible) {
            com.example.project_bookstore.Entity.FlashSaleDetail fs = flashSaleMap.get(b.getBookId());
            if (fs == null) continue;
            if (seenTicker.containsKey(b.getBookId())) continue; // tránh trùng khi 1 sách nằm ở nhiều section
            seenTicker.put(b.getBookId(), true);

            Map<String, Object> item = new HashMap<>();
            item.put("bookId", b.getBookId());
            item.put("title", b.getTitle());
            item.put("picture", b.getPicture());
            item.put("oldPrice", b.getPrice());
            item.put("salePrice", fs.getSalePrice());
            item.put("remaining", fs.getRemaining());
            flashSaleTicker.add(item);
        }
        model.addAttribute("flashSaleTicker", flashSaleTicker);

        return "index";
    }

    @GetMapping("/books/{id}")
    public String bookDetail(@PathVariable("id") String bookId, Model model) {

        Books book = booksService.getBookById(bookId);

        double avgRating = reviewService.getAverageRatingForBook(bookId);
        int avgRatingRounded = reviewService.getAverageRatingRounded(bookId);
        long reviewCount = reviewService.getReviewCountForBook(bookId);
        List<Review> reviews = reviewService.getReviewsForBook(bookId);

        model.addAttribute("book", book);
        model.addAttribute("avgRating", avgRating);
        model.addAttribute("avgRatingRounded", avgRatingRounded);
        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("reviews", reviews);

        List<Books> recommendedBooks =
                recommendationService.recommendBooks(bookId);

        model.addAttribute("recommendedBooks", recommendedBooks);

        Map<String, FlashSaleDetail> flashSaleMap =
                flashSaleService.getActiveSaleMapForBooks(
                        recommendedBooks.stream()
                                .map(Books::getBookId)
                                .toList()
                );

        model.addAttribute("flashSaleMap", flashSaleMap);

        model.addAttribute(
                "flashSaleDetail",
                flashSaleService.getActiveSaleForBook(bookId).orElse(null)
        );

        model.addAttribute(
                "currentFlashSale",
                flashSaleService.getCurrentActive().orElse(null)
        );

        return "book_detail";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "categoryId", required = false) String categoryId,
            Model model,
            HttpServletRequest request
    ) {
        List<Books> books = booksService.searchBooks(q, categoryId);

        Map<String, Double> avgRatings = new HashMap<>();
        Map<String, Long> ratingCounts = new HashMap<>();

        for (Books b : books) {
            int rounded = reviewService.getAverageRatingRounded(b.getBookId());
            avgRatings.put(b.getBookId(), (double) rounded);
            ratingCounts.put(b.getBookId(), reviewService.getReviewCountForBook(b.getBookId()));
        }

        model.addAttribute("books", books);
        model.addAttribute("avgRatings", avgRatings);
        model.addAttribute("ratingCounts", ratingCounts);
        model.addAttribute("q", q);
        model.addAttribute("selectedCategoryId", categoryId);

        String xrw = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equalsIgnoreCase(xrw)) {
            return "search-results :: results";
        }
        return "/home";
    }

}
