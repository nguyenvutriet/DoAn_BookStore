package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.Review;
import com.example.project_bookstore.Repository.IReviewRepository;
import com.example.project_bookstore.Repository.IReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private IReviewRepository repo;

    private final IReviewRepository reviewRepository;

    public ReviewService(IReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /**
     * Điểm trung bình (0–5), ví dụ: 4.2
     */
    public double getAverageRatingForBook(String bookId) {
        Double avg = reviewRepository.findAverageRatingByBookId(bookId);
        return (avg != null) ? avg : 0.0;
    }

    /**
     * Điểm trung bình làm tròn (0–5), ví dụ: 4 (để hiển thị 4 sao)
     */
    public int getAverageRatingRounded(String bookId) {
        return (int) Math.round(getAverageRatingForBook(bookId));
    }

    /**
     * % sao so với 5 sao, ví dụ: 4.2/5 = 84%
     */
    public double getRatingPercent(String bookId) {
        double avg = getAverageRatingForBook(bookId);
        return (avg / 5.0) * 100.0;
    }

    /**
     * Số lượng review của sách
     */
    public long getReviewCountForBook(String bookId) {
        return reviewRepository.countByBook_BookId(bookId);
    }

    /**
     * Danh sách các review của sách
     */
    public List<Review> getReviewsForBook(String bookId) {
        return reviewRepository.findByBook_BookId(bookId);
    }


}
