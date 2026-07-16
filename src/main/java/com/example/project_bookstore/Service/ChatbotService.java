package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.Books;
import com.example.project_bookstore.Entity.Orders;
import com.example.project_bookstore.Repository.IBooksRepository;
import com.example.project_bookstore.Repository.IOrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ChatbotService {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private BooksService booksService;

    @Autowired
    private IBooksRepository booksRepository;

    @Autowired
    private IOrdersRepository ordersRepository;

    public String ask(String message) {

        // Ưu tiên kiểm tra tên sách cụ thể
        String stockResult = findBookStock(message);

        if (stockResult != null) {
            return stockResult;
        }

        // Nếu người dùng nhập thẳng mã đơn
        if (extractOrderId(message) != null) {
            return findOrderStatus(message);
        }

        // Gemini nhận diện ý định
        String intent = geminiService.detectIntent(message);

        switch (intent) {

            case "CHECK_STOCK":
                return getInStockBooks();

            case "BEST_SELLER":
                return getBestSeller();

            case "ORDER_STATUS":
                return findOrderStatus(message);

            default:
                String context =
                        booksService.getCachedContext();

                return geminiService.askGeminiWithContext(
                        message,
                        context
                );
        }
    }

    private String extractOrderId(String message) {

        Pattern pattern =
                Pattern.compile("\\bO\\d+\\b",
                        Pattern.CASE_INSENSITIVE);

        Matcher matcher =
                pattern.matcher(message);

        if (matcher.find()) {
            return matcher.group().toUpperCase();
        }

        return null;
    }

    private String findOrderStatus(String message) {

        String orderId = extractOrderId(message);

        if (orderId == null) {
            return "Vui lòng cung cấp mã đơn hàng. Ví dụ: O520";
        }

        Optional<Orders> order =
                ordersRepository.findById(orderId);

        if (order.isPresent()) {

            return "📦 Đơn hàng "
                    + order.get().getOrderId()
                    + " hiện đang ở trạng thái: "
                    + order.get().getStatus();
        }

        return "Không tìm thấy đơn hàng " + orderId;
    }

    private String getInStockBooks() {

        List<Books> books = booksRepository.findAllForContext();

        StringBuilder sb = new StringBuilder();

        sb.append("📚 Các sách hiện còn hàng:\n\n");

        int count = 0;

        for (Books book : books) {

            if (book.getQuantity() > 0) {

                sb.append("- ")
                        .append(book.getTitle())
                        .append(" (")
                        .append(book.getQuantity())
                        .append(" cuốn)\n");

                count++;

                if (count >= 10) {
                    break;
                }
            }
        }

        if (count == 0) {
            return "Hiện tại chưa có sách nào còn hàng.";
        }

        return sb.toString();
    }

    private String findBookStock(String message) {

        List<Books> books = booksRepository.findAllForContext();

        String lower = message.toLowerCase();

        for (Books book : books) {

            if (lower.contains(book.getTitle().toLowerCase())) {

                return "📚 Sách \""
                        + book.getTitle()
                        + "\" hiện còn "
                        + book.getQuantity()
                        + " cuốn trong kho.";
            }
        }

        return null;
    }

    private String getBestSeller() {

        List<Books> books =
                booksRepository.findTopBestSelling(
                        PageRequest.of(0, 1)
                );

        if (books.isEmpty()) {
            return "Chưa có dữ liệu bán hàng.";
        }

        Books top = books.get(0);

        return "🔥 Sách bán chạy nhất hiện nay là \""
                + top.getTitle()
                + "\" của tác giả "
                + top.getAuthor();
    }

}
