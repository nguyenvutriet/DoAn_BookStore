package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Entity.Presence;
import com.example.project_bookstore.Service.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/presence")
public class PresenceController {

    private final PresenceService service;

    @GetMapping("/{username}")
    public Presence get(@PathVariable String username) {
        return service.get(username);
    }
}
