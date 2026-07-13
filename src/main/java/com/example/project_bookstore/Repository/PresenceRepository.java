package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.Presence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresenceRepository
        extends JpaRepository<Presence, String> {
}
