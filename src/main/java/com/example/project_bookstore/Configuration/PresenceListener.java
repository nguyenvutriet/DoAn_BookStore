package com.example.project_bookstore.Configuration;

import com.example.project_bookstore.Entity.Presence;
import com.example.project_bookstore.Service.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

@Component
@RequiredArgsConstructor
public class PresenceListener {

    private final PresenceService service;
    private final SimpMessagingTemplate template;

    @EventListener
    public void onConnect(SessionConnectEvent e) {
        if (e.getUser() == null) return;

        Presence p = service.setOnline(e.getUser().getName());
        template.convertAndSend("/topic/presence", p);
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent e) {
        if (e.getUser() == null) return;

        Presence p = service.setOffline(e.getUser().getName());
        template.convertAndSend("/topic/presence", p);
    }
}
