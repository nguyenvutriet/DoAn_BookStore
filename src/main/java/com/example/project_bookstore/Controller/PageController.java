package com.example.project_bookstore.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/chat")
    public String chatPage(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        return "chat";
    }
}
