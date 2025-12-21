package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Entity.Books;
import com.example.project_bookstore.Entity.Review;
import com.example.project_bookstore.Service.BooksService;
import com.example.project_bookstore.Service.ReviewService;
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


    @GetMapping()
    public String home(
            @RequestParam(value = "categoryId", required = false) String categoryId,

            @RequestParam(value = "pageNew",  defaultValue = "0") int pageNew,
            @RequestParam(value = "pageBest", defaultValue = "0") int pageBest,
            @RequestParam(value = "pageFav",  defaultValue = "0") int pageFav,

            @RequestParam(value = "size", defaultValue = "8") int size,
            Model model
    ) {
        Pageable pNew  = PageRequest.of(pageNew,  size);
        Pageable pBest = PageRequest.of(pageBest, size);
        Pageable pFav  = PageRequest.of(pageFav,  size);

        Page<Books> newBooksPage = booksService.getNewBooksPage(categoryId, pNew);
        Page<Books> bestSellingBooksPage = booksService.getBestSellingBooksPage(categoryId, pBest);
        Page<Books> favoriteBooksPage = booksService.getFavoriteBooksPage(categoryId, pFav);

        // Rating chỉ tính cho sách đang hiển thị
        Map<String, Double> avgRatings = new HashMap<>();
        Map<String, Long> ratingCounts = new HashMap<>();

        List<Books> visible = new ArrayList<>();
        visible.addAll(newBooksPage.getContent());
        visible.addAll(bestSellingBooksPage.getContent());
        visible.addAll(favoriteBooksPage.getContent());

        for (Books b : visible) {
            int rounded = reviewService.getAverageRatingRounded(b.getBookId());
            avgRatings.put(b.getBookId(), (double) rounded);
            ratingCounts.put(b.getBookId(), reviewService.getReviewCountForBook(b.getBookId()));
        }

        model.addAttribute("selectedCategoryId", categoryId);

        model.addAttribute("newBooksPage", newBooksPage);
        model.addAttribute("bestSellingBooksPage", bestSellingBooksPage);
        model.addAttribute("favoriteBooksPage", favoriteBooksPage);

        model.addAttribute("newBooks", newBooksPage.getContent());
        model.addAttribute("bestSellingBooks", bestSellingBooksPage.getContent());
        model.addAttribute("favoriteBooks", favoriteBooksPage.getContent());

        model.addAttribute("avgRatings", avgRatings);
        model.addAttribute("ratingCounts", ratingCounts);

        model.addAttribute("pageNew", pageNew);
        model.addAttribute("pageBest", pageBest);
        model.addAttribute("pageFav", pageFav);
        model.addAttribute("size", size);

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
