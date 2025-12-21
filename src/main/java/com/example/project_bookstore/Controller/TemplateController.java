package com.example.project_bookstore.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TemplateController {

    @GetMapping("/chatbot.html") // Hoặc chỉ "/chatbot" nếu muốn đường dẫn đẹp hơn
    public String showChatbot() {
        // Spring Boot sẽ tìm tệp src/main/resources/templates/chatbot.html
        return "chatbot";
    }
}
