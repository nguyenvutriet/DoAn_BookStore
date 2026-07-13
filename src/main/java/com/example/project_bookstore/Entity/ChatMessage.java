package com.example.project_bookstore.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user đang chat
    private String userName;

    // USER | ADMIN
    private String sender;

    @Column(columnDefinition = "TEXT")
    private String content;

    // SENT | SEEN
    private String status;

    private LocalDateTime createdAt;

    // ====== NEW ======
    // TEXT | IMAGE
    @Column(name = "type")
    private String type;

    @Column(name = "image_url")
    private String imageUrl;
}
