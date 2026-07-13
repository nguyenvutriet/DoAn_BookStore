package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.ChatMessage;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByUserNameOrderByCreatedAtAsc(String userName);

    @Query("""
        SELECT c.userName
        FROM ChatMessage c
        WHERE c.userName IS NOT NULL
        GROUP BY c.userName
        ORDER BY MAX(c.createdAt) DESC
    """)
    List<String> findUsersOrderByLastMessage();

    @Query("""
        SELECT COUNT(c)
        FROM ChatMessage c
        WHERE c.userName = :userName
          AND c.sender = 'USER'
          AND c.status = 'SENT'
    """)
    long countUnreadByUser(@Param("userName") String userName);

    @Modifying
    @Query("""
        UPDATE ChatMessage c
        SET c.status = 'SEEN'
        WHERE c.userName = :userName
          AND c.sender = 'USER'
          AND c.status = 'SENT'
    """)
    void markSeen(@Param("userName") String userName);
}
