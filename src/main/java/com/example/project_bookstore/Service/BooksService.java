package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.Books;
import com.example.project_bookstore.Repository.IBooksRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;


import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class BooksService {

    @Autowired
    private IBooksRepository booksRepository;

    private String cachedContext;

    @PostConstruct
    public void init() {
        // Khởi tạo cache context
        cachedContext = buildGeneralContext(2500);
    }

    public String buildGeneralContext(int maxChars) {
        // Sử dụng findAllForContext để lấy 72 cuốn
        List<Books> list = booksRepository.findAllForContext();

        // Giả sử logic buildGeneralContext đã được sửa và chỉ trả về chuỗi nội dung
        String content = formatBooksAsContext(list);
        if (content.length() > maxChars) {
            content = content.substring(0, maxChars) + "... (Context bị cắt ngắn)";
        }

        // ⭐ ĐẶT LỆNH HƯỚNG DẪN ĐẶC BIỆT CHO AI (Hardcode tổng số sách)
        String totalCountInstruction = "TỔNG SỐ SÁCH TRONG CỬA HÀNG LÀ: 72 CUỐN.\n";

        return totalCountInstruction + content;
    }

    public BooksService(IBooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    // LẤY CHI TIẾT SÁCH THEO ID (dùng cho /books/{id})
    public Books getBookById(String bookId) {
        return booksRepository.findById(bookId)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy sách với id = " + bookId));
    }

    // NEW: sort theo publicationYear desc
    public Page<Books> getNewBooksPage(String categoryId, Pageable pageable) {
        if (categoryId == null || categoryId.isBlank()) {
            return booksRepository.findAllByOrderByPublicationYearDesc(pageable);
        }
        return booksRepository.findByCategory_CategoryIdOrderByPublicationYearDesc(categoryId, pageable);
    }

    // BEST-SELLING: tổng quantity OrderDetail desc
    public Page<Books> getBestSellingBooksPage(String categoryId, Pageable pageable) {
        if (categoryId == null || categoryId.isBlank()) {
            return booksRepository.findBestSellingBooks(pageable);
        }
        return booksRepository.findBestSellingBooksByCategory(categoryId, pageable);
    }

    // FAVORITE: avg(review.rating) desc
    public Page<Books> getFavoriteBooksPage(String categoryId, Pageable pageable) {
        if (categoryId == null || categoryId.isBlank()) {
            return booksRepository.findFavoriteBooks(pageable);
        }
        return booksRepository.findFavoriteBooksByCategory(categoryId, pageable);
    }

//    public List<Books> getNewBooks(String categoryId) {
//        if (categoryId == null || categoryId.isBlank()) {
//            // TẤT CẢ: lấy top 8, không filter category
//            return booksRepository.findTop8ByOrderByPublicationYearDesc();
//        } else {
//            // Theo category
//            return booksRepository
//                    .findTop8ByCategory_CategoryIdOrderByPublicationYearDesc(categoryId);
//        }
//    }

    /* ========== BEST-SELLING BOOKS (OrderDetail) ========== */

//    public List<Books> getBestSellingBooks(String categoryId) {
//        if (categoryId == null || categoryId.isBlank()) {
//            // Top 8 bán chạy toàn bộ
//            return booksRepository.findBestSellingBooks(PageRequest.of(0, 8));
//        } else {
//            // Top 8 bán chạy trong 1 category
//            return booksRepository.findBestSellingBooksByCategory(categoryId, PageRequest.of(0, 8));
//        }
//    }
//
//    /* ========== FAVORITE BOOKS (Review.rating) ========== */
//
//    public List<Books> getFavoriteBooks(String categoryId) {
//        if (categoryId == null || categoryId.isBlank()) {
//            // Top 8 được đánh giá cao nhất toàn bộ
//            return booksRepository.findFavoriteBooks(PageRequest.of(0, 8));
//        } else {
//            // Top 8 được đánh giá cao nhất theo category
//            return booksRepository.findFavoriteBooksByCategory(categoryId, PageRequest.of(0, 8));
//        }
//    }

    public List<Books> searchBooks(String q, String categoryId) {
        String keyword = (q == null) ? "" : q.trim();
        String cat = (categoryId == null) ? "" : categoryId.trim();
        return booksRepository.searchBooks(keyword, cat);
    }

    public String getCachedContext() {
        return cachedContext;
    }

    private String formatBooksAsContext(List<Books> list) {
        if (list == null || list.isEmpty()) {
            return "Không có thông tin sách nào liên quan được tìm thấy trong cơ sở dữ liệu.";
        }

        StringBuilder sb = new StringBuilder();
        for (Books b : list) {
            String title = b.getTitle();
            String author = b.getAuthor();
            int yearValue = b.getPublicationYear();
            String year = yearValue > 0 ? String.valueOf(yearValue) : "N/A";
            String category = b.getCategory() != null ? b.getCategory().getCategoryName() : "N/A";

            // ⭐ FIX GIÁ: Đảm bảo giá là số nguyên thuần túy cho AI so sánh
            String priceStr;
            if (b.getPrice() != null) {
                // Chuyển BigDecimal thành chuỗi số nguyên thuần túy
                priceStr = b.getPrice().toBigInteger().toString();
            } else {
                priceStr = "0";
            }

            String line = String.format("Title: %s | Author: %s | Year: %s | Category: %s | Price: %s VNĐ%n",
                    title, author, year, category, priceStr);
            sb.append(line);
        }
        return sb.toString();
    }

}
