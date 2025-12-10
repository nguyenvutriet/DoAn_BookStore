package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.Books;
import com.example.project_bookstore.Entity.Customers;
import com.example.project_bookstore.Entity.Review;
import com.example.project_bookstore.Repository.IBooksRepository;
import com.example.project_bookstore.Repository.IReviewRepository;
import com.example.project_bookstore.Repository.IReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReviewService {

    @Autowired
    private IReviewRepository repo;

    @Autowired
    private IBooksRepository booksRepository;


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

    // ================== PHẦN TẠO REVIEW MỚI ==================

    public Review createReview(String bookId, int rating, String comment, Customers customer) {

        Books book = booksRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với id: " + bookId));

        String newId = generateReviewId();

        Review review = new Review();
        review.setReviewId(newId);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreationDate(new Date());
        review.setCustomer(customer);
        review.setBook(book);

        return reviewRepository.save(review);
    }

    /**
     * Sinh reviewId kiểu R1, R2, R3... (có thể đổi thành R01, R02 nếu muốn)
     */
    private String generateReviewId() {
        List<Review> dsReview = reviewRepository.findAll();
        List<Integer> dsSo = new ArrayList<>();

        // Nếu bảng review đang trống thì bắt đầu từ R1
        if (dsReview.isEmpty()) {
            return "R1";
        }

        for (Review r : dsReview) {
            String rid = r.getReviewId();  // ví dụ "R1", "R10"
            if (rid != null && rid.startsWith("R")) {
                try {
                    int so = Integer.parseInt(rid.substring(1)); // bỏ chữ R
                    dsSo.add(so);
                } catch (NumberFormatException e) {
                    // nếu có reviewId lỗi format thì bỏ qua
                }
            }
        }

        int idMax = dsSo.isEmpty() ? 0 : Collections.max(dsSo);

        String newId;
        Review r2;

        do {
            idMax = idMax + 1;
            // nếu muốn R01/R02: dùng String.format("R%02d", idMax);
            newId = "R" + idMax;
            r2 = reviewRepository.findById(newId).orElse(null);
        } while (r2 != null);

        return newId;
    }
}
