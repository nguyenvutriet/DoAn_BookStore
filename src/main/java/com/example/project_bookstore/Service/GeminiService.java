package com.example.project_bookstore.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String askGeminiWithContext(String userPrompt, String dbContext) {
        String instruction = "Bạn là Trợ lý AI Phân tích, Chuyên nghiệp và Thân thiện của UTE BookStore. Bạn PHẢI luôn luôn trả lời bằng tiếng Việt. "
                + "Ngữ cảnh cung cấp cho bạn là dữ liệu đã được tối ưu hóa (Context-Aware Retrieval) cho câu hỏi này. "
                + "Tổng số sách của cửa hàng là 72 cuốn"
                // ⭐ LỆNH PHÂN TÍCH VÀ ĐƯA RA KẾT LUẬN
                + "1. PHÂN TÍCH TỔNG HỢP: Nếu câu hỏi yêu cầu so sánh (như SÁCH MẮC NHẤT, RẺ NHẤT, BÁN CHẠY NHẤT, GIÁ TRỊ TỐT NHẤT), bạn PHẢI TỰ TÍNH TOÁN và phân tích dữ liệu 'Price: [giá] VNĐ' và các thông tin định lượng khác để đưa ra KẾT LUẬN CUỐI CÙNG (ví dụ: 'X là cuốn sách bán chạy nhất do có tổng số lượng bán là Y'). "

                // ⭐ LỆNH VỀ XU HƯỚNG VÀ TƯ VẤN THÊM
                + "2. TƯ VẤN THÊM: Sau khi trả lời chính xác câu hỏi của người dùng, nếu thích hợp, hãy cung cấp thêm một thông tin liên quan khác (ví dụ: 'Cuốn sách này cũng thuộc thể loại X đang được nhiều người quan tâm'). "

                // ⭐ LỆNH ĐỊNH DẠNG VÀ CÚ PHÁP (FIX LỖI FORMAT)
                + "3. CÚ PHÁP VÀ ĐỊNH DẠNG: Khi liệt kê sách, bạn PHẢI sử dụng định dạng danh sách (Gạch đầu dòng/Bullet Point) để dễ đọc. "

                // ⭐ LỆNH XỬ LÝ DỮ LIỆU ĐẶC BIỆT (Hardcode Tổng số sách)
                + "4. DỮ LIỆU ĐẶC BIỆT: Nếu Context bắt đầu bằng 'TỔNG SỐ SÁCH TRONG CỬA HÀNG LÀ: [số lượng]', bạn PHẢI sử dụng con số đó để trả lời câu hỏi về tổng số lượng sách, không được đếm sách trong Context String."

                + "Nếu thông tin không có hoặc không liên quan, hãy trả lời một cách tích cực và mời khách hàng tìm hiểu thêm về các thể loại chung (Quản lý, Sức Khỏe, v.v.).\n"
                + "--- DỮ LIỆU CƠ SỞ DỮ LIỆU: ---\n"
                + dbContext + "\n"
                + "--- CÂU HỎI NGƯỜI DÙNG: ---\n"
                + userPrompt;

        String finalPrompt = instruction + userPrompt;

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                + apiKey;

        Map<String, Object> message = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", finalPrompt)
                                )
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(message, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            Map<String, Object> res = response.getBody();

            List candidates = (List) res.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return "Lỗi Gemini: Không nhận được phản hồi từ mô hình (candidates rỗng).";
            }
            Map firstCandidate = (Map) candidates.get(0);
            Map content = (Map) firstCandidate.get("content");
            List parts = (List) content.get("parts");
            Map part = (Map) parts.get(0);

            return part.get("text").toString();

        } catch (HttpStatusCodeException httpError) {
            String errorBody = httpError.getResponseBodyAsString();
            String errorDetail = httpError.getStatusCode().toString() + ": " + errorBody.substring(0, Math.min(errorBody.length(), 200));
            return "Lỗi HTTP API Gemini: " + errorDetail;
        } catch (Exception e) {
            return "Lỗi gọi API Gemini: " + e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }
}
