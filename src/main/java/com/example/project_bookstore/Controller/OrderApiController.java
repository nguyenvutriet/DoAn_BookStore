package com.example.project_bookstore.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
public class OrderApiController {

    @GetMapping("/test")
    public String test() {
        return "JWT Success";
    }
}