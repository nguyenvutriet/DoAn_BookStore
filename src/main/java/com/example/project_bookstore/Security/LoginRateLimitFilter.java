package com.example.project_bookstore.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 5;
    private static final int BLOCK_MINUTES = 1;

    private final Map<String, Integer> requestCounts =
            new ConcurrentHashMap<>();

    private final Map<String, LocalDateTime> blockedIps =
            new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().equals("/process-login")) {

            String ip = request.getRemoteAddr();

            // đang bị khóa
            if (blockedIps.containsKey(ip)) {

                LocalDateTime blockedUntil =
                        blockedIps.get(ip);

                if (LocalDateTime.now()
                        .isBefore(blockedUntil)) {

                    response.setStatus(429);

                    response.getWriter().write(
                            "IP blocked until "
                                    + blockedUntil
                    );

                    return;
                }

                blockedIps.remove(ip);
                requestCounts.remove(ip);
            }

            int count =
                    requestCounts.getOrDefault(ip, 0) + 1;

            requestCounts.put(ip, count);

            System.out.println(
                    "IP = "
                            + ip
                            + " COUNT = "
                            + count
            );

            if (count > MAX_REQUESTS) {

                LocalDateTime blockedUntil =
                        LocalDateTime.now()
                                .plusMinutes(BLOCK_MINUTES);

                blockedIps.put(ip, blockedUntil);

                System.out.println(
                        "BLOCKED IP: "
                                + ip
                                + " until "
                                + blockedUntil
                );

                response.setStatus(429);

                response.getWriter().write(
                        "Too many login attempts. Try again in "
                                + BLOCK_MINUTES
                                + " minute(s)."
                );

                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}