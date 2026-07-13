package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.ChatMessage;
import com.example.project_bookstore.Repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository repo;

    // USER gửi text
    public ChatMessage saveUser(String userName, String content) {
        ChatMessage msg = new ChatMessage();
        msg.setUserName(userName);
        msg.setSender("USER");
        msg.setContent(content);
        msg.setType("TEXT");
        msg.setStatus("SENT");
        msg.setCreatedAt(LocalDateTime.now());
        return repo.save(msg);
    }

    // USER gửi ảnh
    public ChatMessage saveUserImage(String userName, String imageUrl) {
        ChatMessage msg = new ChatMessage();
        msg.setUserName(userName);
        msg.setSender("USER");
        msg.setType("IMAGE");
        msg.setImageUrl(imageUrl);
        msg.setStatus("SENT");
        msg.setCreatedAt(LocalDateTime.now());
        return repo.save(msg);
    }

    // ADMIN gửi text
    public ChatMessage saveAdmin(String userName, String content) {
        ChatMessage msg = new ChatMessage();
        msg.setUserName(userName);
        msg.setSender("ADMIN");
        msg.setContent(content);
        msg.setType("TEXT");
        msg.setStatus("SEEN");
        msg.setCreatedAt(LocalDateTime.now());
        return repo.save(msg);
    }

    // ADMIN gửi ảnh
    public ChatMessage saveAdminImage(String userName, String imageUrl) {
        System.out.println(">>> saveAdminImage()");
        System.out.println("userName = " + userName);
        System.out.println("imageUrl = " + imageUrl);

        ChatMessage msg = new ChatMessage();
        msg.setUserName(userName);
        msg.setSender("ADMIN");
        msg.setType("IMAGE");
        msg.setImageUrl(imageUrl);
        msg.setStatus("SEEN");
        msg.setCreatedAt(LocalDateTime.now());

        ChatMessage saved = repo.save(msg);
        System.out.println(">>> saved id = " + saved.getId());

        return saved;
    }


    public List<ChatMessage> history(String userName) {
        return repo.findByUserNameOrderByCreatedAtAsc(userName);
    }

    public List<String> allChatUsers() {
        return repo.findUsersOrderByLastMessage()
                .stream()
                .filter(u -> !u.equalsIgnoreCase("admin"))
                .toList();
    }

    public long unreadCount(String userName) {
        return repo.countUnreadByUser(userName);
    }

    @Transactional
    public void markSeen(String userName) {
        repo.markSeen(userName);
    }
}
