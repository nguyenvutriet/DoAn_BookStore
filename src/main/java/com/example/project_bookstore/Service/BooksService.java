package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.Books;
import com.example.project_bookstore.Repository.IBooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

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

    /* ========== BEST-SELLING BOOKS (OrderDetail) ========== */

    public List<Books> getBestSellingBooks(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) {
            // Top 8 bán chạy toàn bộ
            return booksRepository.findBestSellingBooks(PageRequest.of(0, 8));
        } else {
            // Top 8 bán chạy trong 1 category
            return booksRepository.findBestSellingBooksByCategory(categoryId, PageRequest.of(0, 8));
        }
    }

    /* ========== FAVORITE BOOKS (Review.rating) ========== */

    public List<Books> getFavoriteBooks(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) {
            // Top 8 được đánh giá cao nhất toàn bộ
            return booksRepository.findFavoriteBooks(PageRequest.of(0, 8));
        } else {
            // Top 8 được đánh giá cao nhất theo category
            return booksRepository.findFavoriteBooksByCategory(categoryId, PageRequest.of(0, 8));
        }
    }


}
