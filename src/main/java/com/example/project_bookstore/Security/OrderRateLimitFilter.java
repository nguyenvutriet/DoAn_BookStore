package com.example.project_bookstore.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderRateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Long> lastOrderTime =
            new ConcurrentHashMap<>();

    private final Map<String, Integer> orderCount =
            new ConcurrentHashMap<>();

    private final Map<String, Long> windowStart =
            new ConcurrentHashMap<>();

    private static final long MIN_INTERVAL = 10_000; // 10 giây
    private static final long WINDOW = 60_000;       // 1 phút
    private static final int MAX_ORDERS = 3;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().equals("/checkout/submit")
                && request.getMethod().equalsIgnoreCase("POST")) {

            String ip = request.getRemoteAddr();
            long now = System.currentTimeMillis();

            // Chặn mua quá nhanh
            Long lastTime = lastOrderTime.get(ip);

            if (lastTime != null
                    && now - lastTime < MIN_INTERVAL) {

                response.setStatus(429);

                response.getWriter().write(
                        "Please wait before placing another order."
                );

                return;
            }

            // Khởi tạo cửa sổ thời gian
            windowStart.putIfAbsent(ip, now);

            // Hết 1 phút thì reset
            if (now - windowStart.get(ip) > WINDOW) {

                windowStart.put(ip, now);
                orderCount.put(ip, 0);
            }

            int count =
                    orderCount.getOrDefault(ip, 0) + 1;

            orderCount.put(ip, count);

            if (count > MAX_ORDERS) {

                response.setStatus(429);

                response.getWriter().write(
                        "Too many orders. Try again later."
                );

                return;
            }

            lastOrderTime.put(ip, now);

            System.out.println(
                    "IP=" + ip +
                            " Orders=" + count
            );
        }

        filterChain.doFilter(request, response);
    }
}