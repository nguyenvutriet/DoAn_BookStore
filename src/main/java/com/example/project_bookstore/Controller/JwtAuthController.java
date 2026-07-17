package com.example.project_bookstore.Controller;

import com.example.project_bookstore.dto.LoginRequest;
import com.example.project_bookstore.dto.LoginResponse;
import com.example.project_bookstore.Entity.Users;
import com.example.project_bookstore.Security.JwtService;
import com.example.project_bookstore.Service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class JwtAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsersService usersService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request
    ) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        Users user =
                usersService.getUserByUserName(
                        request.getUsername()
                );

        String token =
                jwtService.generateToken(
                        user.getUserName(),
                        user.getRole()
                );

        return new LoginResponse(token);
    }
}
