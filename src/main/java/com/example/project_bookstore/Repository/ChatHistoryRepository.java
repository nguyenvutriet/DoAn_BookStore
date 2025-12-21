package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    List<ChatHistory> findByCustomerIdOrderByCreatedAtAsc(String customerId);
    List<ChatHistory> findBySessionIdOrderByCreatedAtAsc(String sessionId);
}
