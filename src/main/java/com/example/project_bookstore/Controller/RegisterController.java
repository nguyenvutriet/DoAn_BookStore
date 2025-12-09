package com.example.project_bookstore.Controller;


import com.example.project_bookstore.Entity.Cart;
import com.example.project_bookstore.Entity.Customers;
import com.example.project_bookstore.Service.CartService;
import com.example.project_bookstore.Service.CustomersService;
import com.example.project_bookstore.Service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Autowired
    private CartService cartService;

    @GetMapping
    public String registerUser(){
        return "register-form";
    }

    @PostMapping("/save")
    public String saveUser(@RequestParam String fullName, @RequestParam String email, @RequestParam String phone, @RequestParam String address, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOfBirth, @RequestParam String userName, @RequestParam String password, @RequestParam String confirm, @RequestParam String role, Model model){

        if(!password.equals(confirm)){
            model.addAttribute("error","Passwords do not match");
            return "redirect:/register";
        }
        Customers cu = customersService.getCustomerByEmail(email);
        if(cu!=null){
            model.addAttribute("error","Email already exists");
            return "redirect:/register";
        }

        customersService.save(fullName, email, address, dateOfBirth, phone);

        Customers cus = customersService.getCustomerByEmail(email);

        BCryptPasswordEncoder  passwordEncoder = new BCryptPasswordEncoder();
        password = passwordEncoder.encode(password);

        boolean flag = usersService.saveUser(userName, password, fullName, role, cus);
        cartService.createCart(cus);

        if(!flag){
            model.addAttribute("error","User already exists");
            model.addAttribute("fullName",fullName);
            model.addAttribute("email",email);
            model.addAttribute("phone",phone);
            model.addAttribute("address",address);
            model.addAttribute("role",role);
            model.addAttribute("dateOfBirth",dateOfBirth);
            return "register-form";
        }
        return "redirect:/login";
    }
}
