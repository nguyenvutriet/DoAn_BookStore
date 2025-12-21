package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IReviewRepository extends JpaRepository<Review, String> {
    long count();

    // Lấy tất cả review của 1 cuốn sách
    List<Review> findByBook_BookId(String bookId);

    // Lấy các review của 1 khách hàng
    List<Review> findByCustomer_CustomerId(String customerId);

    // Đếm số review của 1 cuốn sách
    long countByBook_BookId(String bookId);

    // Tính điểm trung bình rating cho 1 cuốn sách
    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.book.bookId = :bookId")
    Double findAverageRatingByBookId(@Param("bookId") String bookId);

    // Lấy review theo customer + book (để kiểm tra đã đánh giá hay chưa)
    Review findByCustomer_CustomerIdAndBook_BookId(String customerId, String bookId);

}
