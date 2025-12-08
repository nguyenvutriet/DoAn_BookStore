package com.example.project_bookstore.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class BookStoreSecurity {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())  // ⚡ tắt CSRF để fetch POST không bị 403
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/cart/update").permitAll()   // cho phép API update
                        .anyRequest().permitAll()                         // cho phép toàn app
                );

        return http.build();
    }
}
