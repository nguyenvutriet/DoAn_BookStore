package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Entity.ChatHistory;
import com.example.project_bookstore.Repository.ChatHistoryRepository;
import com.example.project_bookstore.Service.ChatbotService;
import com.example.project_bookstore.Service.GeminiService;
import com.example.project_bookstore.Service.BooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatbotService chatbotService;

    @Autowired
    private ChatHistoryRepository chatRepo;

    @Autowired
    private BooksService booksService;


    // =======================================
    // GỬI TIN NHẮN
    // =======================================
    @PostMapping("/send")
    public Map<String, Object> send(
            @RequestParam String customerId,
            @RequestParam String message
    ) {
        if (customerId == null || customerId.isEmpty() || "GUEST".equalsIgnoreCase(customerId)) {
            throw new IllegalArgumentException("Bạn cần đăng nhập để chat với trợ lý AI");
        }

        // Lưu tin user
        ChatHistory userMsg = new ChatHistory();
        userMsg.setCustomerId(customerId);
        userMsg.setSessionId(customerId);
        userMsg.setRole("user");
        userMsg.setMessage(message);
        chatRepo.save(userMsg);

        String reply = chatbotService.ask(message);

        // Lưu tin bot
        ChatHistory botMsg = new ChatHistory();
        botMsg.setCustomerId(customerId);
        botMsg.setSessionId(customerId);
        botMsg.setRole("bot");
        botMsg.setMessage(reply);
        chatRepo.save(botMsg);

        return Map.of("reply", reply);
    }


    // =======================================
    // LỊCH SỬ CHAT
    // =======================================
    @GetMapping("/history")
    public List<ChatHistory> history(@RequestParam String customerId) {
        if (customerId == null || customerId.isEmpty() || "GUEST".equalsIgnoreCase(customerId)) {
            throw new IllegalArgumentException("Bạn cần đăng nhập để xem lịch sử chat");
        }
        return chatRepo.findByCustomerIdOrderByCreatedAtAsc(customerId);
    }
}
