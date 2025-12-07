package com.example.project_bookstore.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {

    @GetMapping
    public String home(){
        return "index";
    }

    @Controller
    public class LoginController {
        @GetMapping("/login_form.html")
        public String loginForm() {
            return "login_form"; // Tên của file login-form.html mà bạn muốn trả về
        }
    }

    @Controller
    public class RegisterController {
        @GetMapping("/register-form.html")
        public String loginForm() {
            return "register-form"; // Tên của file login-form.html mà bạn muốn trả về
        }
    }

}
