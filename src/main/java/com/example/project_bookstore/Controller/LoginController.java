package com.example.project_bookstore.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginForm(){
        return "login_form";
    }

    @GetMapping("/error403")
    public String error403(){
        return "redirect:/home";
    }


}
