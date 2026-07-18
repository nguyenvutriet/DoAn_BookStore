package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.Books;
import com.example.project_bookstore.Entity.Review;
import com.example.project_bookstore.Repository.IBooksRepository;
import com.example.project_bookstore.Repository.IReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewSummaryService {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private IReviewRepository reviewRepository;

    @Autowired
    private IBooksRepository booksRepository;

    public void updateBookSummary(String bookId){

        List<Review> reviews =
                reviewRepository.findByBook_BookId(bookId);

        if(reviews.isEmpty()){
            return;
        }

        StringBuilder sb = new StringBuilder();

        for(Review r : reviews){

            sb.append("Số sao: ")
                    .append(r.getRating())
                    .append("\n");

            sb.append(r.getComment())
                    .append("\n\n");
        }

        String prompt =
                """
                        Bạn là AI của nhà sách.
                        
                                 Dựa trên các đánh giá của khách hàng, hãy viết một đoạn tóm tắt ngắn để hiển thị trên trang sản phẩm.
                        
                                 Yêu cầu:
                        
                                 - Viết tự nhiên như Shopee hoặc Amazon.
                                 - Không dùng tiêu đề như "Ưu điểm", "Nhược điểm", "Kết luận".
                                 - Không xưng "tôi", "AI".
                                 - Không dùng markdown, dấu * hoặc bullet.
                                 - Chỉ 2-4 câu.
                                 - Nếu chưa có nhược điểm thì chỉ nói "Hiện chưa ghi nhận nhiều phản hồi tiêu cực."
                                 - Trả lời hoàn toàn bằng tiếng Việt.
                        
                                 Đánh giá:

                """
                        + sb;

        String summary =
                geminiService.askGemini(prompt);

        Books book =
                booksRepository.findById(bookId).orElse(null);

        if(book!=null){

            book.setAiSummary(summary);

            booksRepository.save(book);

        }

    }

}
