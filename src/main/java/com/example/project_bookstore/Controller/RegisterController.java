package com.example.project_bookstore.Controller;


import com.example.project_bookstore.Entity.Customers;
import com.example.project_bookstore.Service.CustomersService;
import com.example.project_bookstore.Service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private CustomersService  customersService;

    @Autowired
    private UsersService usersService;

    @GetMapping
    public String registerUser(){
        return "register-form";
    }

    @PostMapping("/save")
    public String saveUser(@RequestParam String fullName, @RequestParam String email, @RequestParam String phone, @RequestParam String address, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOfBirth, @RequestParam String userName, @RequestParam String password, @RequestParam String confirm, @RequestParam String role, Model model){
        if(!password.equals(confirm)){
            model.addAttribute("error","Passwords do not match");
            return "register";
        }
        boolean flag = customersService.save(fullName, email, address, dateOfBirth, phone);

        if(!flag){
            model.addAttribute("error","Customer not found");
            return "register";
        }

        Customers cus = customersService.getCustomerByEmail(email);
        System.out.println(cus.toString());

        usersService.saveUser(userName, password, fullName, role, cus);

        return "redirect:/login";
    }
}
