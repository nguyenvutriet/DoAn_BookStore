package com.example.project_bookstore.Security;

import com.example.project_bookstore.Entity.Customers;
import com.example.project_bookstore.Entity.Users;
import com.example.project_bookstore.Service.CustomOAuth2UserService;
import com.example.project_bookstore.Service.CustomersService;
import com.example.project_bookstore.Service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.List;

@Configuration
public class BookStoreSecurity {

    @Autowired
    private UsersService usersService;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private LoginRateLimitFilter loginRateLimitFilter;

    @Autowired
    private OrderRateLimitFilter orderRateLimitFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UsersService usersService) {
        DaoAuthenticationProvider dao = new DaoAuthenticationProvider(usersService);
        dao.setPasswordEncoder(passwordEncoder());
        return dao;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/home","/api/books/*/preview", "/home/**", "/books/**", "/home/books/**", "/register/**", "/login", "/css/**", "/js/**", "/images/**", "/images/chat-upload/**", "/api/**", "/recommend/**", "/").permitAll()
                        // ===== 🔥 WEBSOCKET & STOMP (BẮT BUỘC) =====
                        .requestMatchers(
                                "/ws/**",
                                "/app/**",
                                "/topic/**",
                                "/queue/**"
                        ).permitAll()

                        // ===== CHAT (BẮT BUỘC LOGIN) =====
                        .requestMatchers("/chat", "/chat/**").authenticated()

                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/home/order").hasRole("USER")
                        .requestMatchers("/gio_hang/**").hasRole("USER")
                        .requestMatchers("/user/**").hasRole("USER")
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/process-login")
                        .successHandler(urlHandler())
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .oauth2Login(
                        oauth -> oauth.loginPage("/login")
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(oAuth2UserService())
                                )
                                .successHandler(oauth2UrlHandler())
                                .failureHandler(oAuth2FailureHandler())
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/home")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll()
                ).exceptionHandling(
                        auth -> auth.accessDeniedPage("/error403")
                )
                .sessionManagement(
                        session -> session.sessionFixation().newSession()
                );

        http.addFilterBefore(
                jwtFilter,
                org.springframework.security.web.authentication
                        .UsernamePasswordAuthenticationFilter.class
        );

        http.addFilterBefore(
                loginRateLimitFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        http.addFilterBefore(
                orderRateLimitFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler urlHandler() {
        return (request, response, authentication) -> {
            var authorities = authentication.getAuthorities();

            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                response.sendRedirect("/admin/dashboard");
            } else {
                response.sendRedirect("/home");
            }
        };
    }

    @Bean
    public AuthenticationSuccessHandler oauth2UrlHandler() {
        return (request, response, authentication) -> {

            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            String email = oAuth2User.getAttribute("email");

            Customers customer = customersService.getCustomerByEmail(email);

            if (customer == null) {
                request.getSession().invalidate();
                response.sendRedirect("/register/infor");
                return;
            }

            Users user = usersService.getUserByCustomerId(customer.getCustomerId());

            Collection<GrantedAuthority> authorities = List.of( new SimpleGrantedAuthority(user.getRole()));

            UserDetails userDetails = new User(user.getUserName(), user.getPassword(), authorities);

            Authentication newAuth =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            authorities
                    );

            SecurityContextHolder.getContext().setAuthentication(newAuth);
            request.getSession().setMaxInactiveInterval(0);

            response.sendRedirect("/home");
        };
    }


    @Bean
    public AuthenticationFailureHandler oAuth2FailureHandler() {
        return (request, response, exception) -> {

            if (exception instanceof OAuth2RegistrationException) {

                String message = exception.getMessage();

                if(message.equals("User not found")){
                    response.sendRedirect("/register/infor");
                }

                if(message.equals("Customer not found")){
                    response.sendRedirect("/register/infor");
                }
            } else {
                response.sendRedirect("/login?oauth_error=true");
            }
        };
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return new CustomOAuth2UserService();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {

        return config.getAuthenticationManager();
    }

}
