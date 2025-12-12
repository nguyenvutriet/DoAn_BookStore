package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Entity.Books;
import com.example.project_bookstore.Entity.Review;
import com.example.project_bookstore.Service.BooksService;
import com.example.project_bookstore.Service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public String home(
            @RequestParam(value = "categoryId", required = false) String categoryId,
            Model model
    ) {
        List<Books> newBooks = booksService.getNewBooks(categoryId);
        List<Books> bestSellingBooks = booksService.getBestSellingBooks(categoryId);
        List<Books> favoriteBooks = booksService.getFavoriteBooks(categoryId);

        Map<String, Double> avgRatings = new HashMap<>();
        Map<String, Long> ratingCounts = new HashMap<>();

        // GỘP 3 LIST vào 1 vòng lặp chung
        List<Books> allBooks = new java.util.ArrayList<>();
        allBooks.addAll(newBooks);
        allBooks.addAll(bestSellingBooks);
        allBooks.addAll(favoriteBooks);

        for (Books b : allBooks) {
            int rounded = reviewService.getAverageRatingRounded(b.getBookId());
            avgRatings.put(b.getBookId(), (double) rounded);
            ratingCounts.put(b.getBookId(), reviewService.getReviewCountForBook(b.getBookId()));
        }

        model.addAttribute("newBooks", newBooks);
        model.addAttribute("bestSellingBooks", bestSellingBooks);
        model.addAttribute("favoriteBooks", favoriteBooks);

        model.addAttribute("avgRatings", avgRatings);
        model.addAttribute("ratingCounts", ratingCounts);

        model.addAttribute("selectedCategoryId", categoryId);

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




}
