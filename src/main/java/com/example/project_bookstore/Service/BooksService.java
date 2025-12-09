package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.Books;
import com.example.project_bookstore.Repository.IBooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BooksService {

    @Autowired
    private IBooksRepository booksRepository;

    public BooksService(IBooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    // LẤY CHI TIẾT SÁCH THEO ID (dùng cho /books/{id})
    public Books getBookById(String bookId) {
        return booksRepository.findById(bookId)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy sách với id = " + bookId));
    }

    public List<Books> getNewBooks(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) {
            // TẤT CẢ: lấy top 8, không filter category
            return booksRepository.findTop8ByOrderByPublicationYearDesc();
        } else {
            // Theo category
            return booksRepository
                    .findTop8ByCategory_CategoryIdOrderByPublicationYearDesc(categoryId);
        }
    }

    public List<Books> getBestSellingBooks(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) {
            return booksRepository.findTop8ByOrderByQuantityDesc();
        }
        return booksRepository.findTop8ByCategory_CategoryIdOrderByQuantityDesc(categoryId);
    }

    public List<Books> getFavoriteBooks(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) {
            return booksRepository.findTop8ByOrderByPriceDesc();
        }
        return booksRepository.findTop8ByCategory_CategoryIdOrderByPriceDesc(categoryId);
    }
}
