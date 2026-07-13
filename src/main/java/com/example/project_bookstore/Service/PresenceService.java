package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.Presence;
import com.example.project_bookstore.Repository.PresenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PresenceService {

    private final PresenceRepository repo;

    public Presence setOnline(String username) {
        Presence p = repo.findById(username)
                .orElse(new Presence(username, true, LocalDateTime.now()));

        p.setOnline(true);
        p.setLastSeen(LocalDateTime.now());
        return repo.save(p);
    }

    public Presence setOffline(String username) {
        Presence p = repo.findById(username)
                .orElse(new Presence(username, false, LocalDateTime.now()));

        p.setOnline(false);
        p.setLastSeen(LocalDateTime.now());
        return repo.save(p);
    }

    public Presence get(String username) {
        return repo.findById(username)
                .orElse(new Presence(username, false, null));
    }
}
