package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Entity.Books;
import com.example.project_bookstore.Service.BooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private BooksService booksService;

    @GetMapping
    public String home(
            @RequestParam(value = "categoryId", required = false) String categoryId,
            Model model
    ) {
        List<Books> newBooks = booksService.getNewBooks(categoryId);
        List<Books> bestSellingBooks = booksService.getBestSellingBooks(categoryId);
        List<Books> favoriteBooks = booksService.getFavoriteBooks(categoryId);

        System.out.println("=== HOME CONTROLLER ===");
        System.out.println("categoryId = " + categoryId);
        System.out.println("newBooks size = " + (newBooks != null ? newBooks.size() : -1));
        System.out.println("bestSellingBooks size = " + (bestSellingBooks != null ? bestSellingBooks.size() : -1));
        System.out.println("favoriteBooks size = " + (favoriteBooks != null ? favoriteBooks.size() : -1));


        model.addAttribute("newBooks", newBooks);
        model.addAttribute("bestSellingBooks", bestSellingBooks);
        model.addAttribute("favoriteBooks", favoriteBooks);
        model.addAttribute("selectedCategoryId", categoryId);

        return "index";
    }

    @GetMapping("/books/{id}")
    public String bookDetail(@PathVariable("id") String bookId, Model model) {
        Books book = booksService.getBookById(bookId);
        model.addAttribute("book", book);
        return "book_detail";
    }

}
