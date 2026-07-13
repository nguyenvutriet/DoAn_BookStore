package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Entity.ChatMessage;
import com.example.project_bookstore.Service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate template;

    // ===== USER SEND TEXT =====
    @MessageMapping("/chat.send")
    public void userSend(@Payload Map<String, String> payload,
                         Principal principal) {

        String sender = principal.getName();
        if (sender.startsWith("admin")) return;

        ChatMessage saved =
                chatService.saveUser(sender, payload.get("content"));

        template.convertAndSend("/topic/messages." + sender, saved);
        template.convertAndSend("/topic/admin", saved);
    }

    // ===== ADMIN SEND TEXT =====
    @MessageMapping("/chat.admin.send")
    public void adminSend(@Payload Map<String, String> payload) {

        ChatMessage saved =
                chatService.saveAdmin(
                        payload.get("userName"),
                        payload.get("content")
                );

        template.convertAndSend("/topic/messages." + saved.getUserName(), saved);
        template.convertAndSend("/topic/admin", saved);
    }

    // ===== UPLOAD IMAGE =====
    @PostMapping("/chat/upload-image")
    @ResponseBody
    public ChatMessage uploadImage(
            @RequestParam MultipartFile file,
            @RequestParam String userName,
            Principal principal
    ) throws Exception {

        String sender =
                principal != null && principal.getName().startsWith("admin")
                        ? "ADMIN"
                        : "USER";

        // ===== 1️⃣ ĐỌC FILE NGAY (SYNC) =====
        byte[] bytes = file.getBytes(); // 🔥 BẮT BUỘC

        String ext = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID() + ext;

        Path uploadPath = Paths.get(
                System.getProperty("user.dir"),
                "src/main/resources/static/images/chat-upload"
        );
        Files.createDirectories(uploadPath);

        Path filePath = uploadPath.resolve(fileName);

        // ===== 2️⃣ GHI FILE (SYNC – NHANH) =====
        Files.write(filePath, bytes);

        String imageUrl = "/images/chat-upload/" + fileName;

        // ===== 3️⃣ LƯU DB =====
        ChatMessage msg = sender.equals("ADMIN")
                ? chatService.saveAdminImage(userName, imageUrl)
                : chatService.saveUserImage(userName, imageUrl);

        // ===== 4️⃣ WS =====
        template.convertAndSend("/topic/messages." + userName, msg);
        template.convertAndSend("/topic/admin", msg);

        return msg;
    }

    @GetMapping("/chat/history/{userName}")
    @ResponseBody
    public java.util.List<ChatMessage> history(@PathVariable String userName) {
        return chatService.history(userName);
    }

    // ======================
// USER đang gõ → ADMIN
// ======================
    @MessageMapping("/chat.typing.user")
    public void userTyping(Principal principal) {
        String userName = principal.getName();

        template.convertAndSend(
                "/topic/typing.user." + userName,
                userName
        );
    }

    // ======================
// ADMIN đang gõ → USER
// ======================
    @MessageMapping("/chat.typing.admin")
    public void adminTyping(@Payload Map<String, String> payload) {
        String userName = payload.get("userName");

        template.convertAndSend(
                "/topic/typing.admin." + userName,
                "Admin"
        );
    }

}
