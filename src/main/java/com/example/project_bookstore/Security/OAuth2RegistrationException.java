package com.example.project_bookstore.Security;


import org.springframework.security.core.AuthenticationException;

public class OAuth2RegistrationException extends AuthenticationException {
    public OAuth2RegistrationException(String msg) {
        super(msg);
    }
}
