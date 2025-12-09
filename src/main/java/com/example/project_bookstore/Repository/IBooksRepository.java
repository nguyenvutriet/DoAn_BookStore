package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.Books;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface IBooksRepository extends JpaRepository<Books, String> {
    long count();

    // Lấy tất cả sách theo categoryId
    List<Books> findByCategory_CategoryId(String categoryId);

    // ========= NEW BOOKS =========
    // Không filter category
    List<Books> findTop8ByOrderByPublicationYearDesc();

    // Có filter category
    List<Books> findTop8ByCategory_CategoryIdOrderByPublicationYearDesc(String categoryId);

    /* ========== BEST-SELLING BOOKS (OrderDetail) ========== */

    // Top sách bán chạy nhất theo tổng quantity trong OrderDetail (không filter category)
    @Query("""
           SELECT b
           FROM Books b
           JOIN b.orderDetail_Book od
           GROUP BY b
           ORDER BY SUM(od.quantity) DESC
           """)
    List<Books> findBestSellingBooks(Pageable pageable);

    // Top sách bán chạy nhất theo category
    @Query("""
           SELECT b
           FROM Books b
           JOIN b.orderDetail_Book od
           WHERE b.category.categoryId = :categoryId
           GROUP BY b
           ORDER BY SUM(od.quantity) DESC
           """)
    List<Books> findBestSellingBooksByCategory(@Param("categoryId") String categoryId,
                                               Pageable pageable);


    /* ========== FAVORITE BOOKS (Review.rating) ========== */

    // Top sách được đánh giá cao nhất (AVG(rating) desc, không filter category)
    @Query("""
           SELECT b
           FROM Books b
           LEFT JOIN b.reviews r
           GROUP BY b
           ORDER BY COALESCE(AVG(r.rating), 0) DESC, COUNT(r) DESC
           """)
    List<Books> findFavoriteBooks(Pageable pageable);

    // Top sách được đánh giá cao nhất theo category
    @Query("""
           SELECT b
           FROM Books b
           LEFT JOIN b.reviews r
           WHERE b.category.categoryId = :categoryId
           GROUP BY b
           ORDER BY COALESCE(AVG(r.rating), 0) DESC, COUNT(r) DESC
           """)
    List<Books> findFavoriteBooksByCategory(@Param("categoryId") String categoryId,
                                            Pageable pageable);
}
