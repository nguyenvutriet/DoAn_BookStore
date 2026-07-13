package com.example.project_bookstore.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Presence {

    @Id
    private String username;

    private boolean online;

    private LocalDateTime lastSeen;
}
