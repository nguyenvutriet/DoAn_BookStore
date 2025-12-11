package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Entity.Customers;
import com.example.project_bookstore.Entity.Orders;
import com.example.project_bookstore.Entity.Users;
import com.example.project_bookstore.Service.CustomersService;
import com.example.project_bookstore.Service.OrdersService;
import com.example.project_bookstore.Service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private OrdersService ordersService;

    @GetMapping("/profile")
    public String inforCustomer(@AuthenticationPrincipal UserDetails userDetails, Model model){
        String username =  userDetails.getUsername();
        Users user = usersService.getUserByUserName(username);
        Customers cus = customersService.getCustomerById(user.getCustomer().getCustomerId());

        model.addAttribute("user",user);
        model.addAttribute("customer",cus);
        return "inforCustomer";
    }

    @PostMapping("/profile/update")
    public String updateCustomer(@RequestParam String customerId, @RequestParam String fullName, @RequestParam String phone, @RequestParam String email, @RequestParam String address, @RequestParam String birthDate){

        Customers cus = customersService.getCustomerById(customerId);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dob = sdf.parse(birthDate);
            cus.setFullName(fullName);
            cus.setPhone(phone);
            cus.setEmail(email);
            cus.setAddress(address);
            cus.setDateOfBirth(dob);

            customersService.update(cus);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        String username = usersService.getUserByCustomerId(customerId).getUserName();
        return "redirect:/user/profile/" + username;
    }

    @GetMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails, Model model){

        String username =  userDetails.getUsername();
        Users user = usersService.getUserByUserName(username);
        if(user==null){
            return "/login";
        }
        model.addAttribute("user",user);
        return "changePass";
    }

    @PostMapping("/change-password")
    public String updatePassword(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("password") String password, @RequestParam("confirm") String confirm, Model model, RedirectAttributes redirectAttributes){

        String username = userDetails.getUsername();

        if(!password.equals(confirm)){
            redirectAttributes.addFlashAttribute("error", "Mật khẩu không khớp");
            return "redirect:/user/change-password";
        }

        BCryptPasswordEncoder  passwordEncoder = new BCryptPasswordEncoder();
        Users user = usersService.getUserByUserName(username);



        if(passwordEncoder.matches(password, user.getPassword())){
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới không được trùng mật khẩu cũ");
            return "redirect:/user/change-password";
        }

        password =  passwordEncoder.encode(password);
        usersService.updateUser(username, password);
        return "redirect:/login";
    }


    @GetMapping("/myOrder")
    public String pageMyOrder(@AuthenticationPrincipal UserDetails userDetails, Model model){
        String username =   userDetails.getUsername();
        Users user = usersService.getUserByUserName(username);
        Customers cus = customersService.getCustomerById(user.getCustomer().getCustomerId());

        List<Orders> listOrderByCustomerId = ordersService.getOrders(cus.getCustomerId());

        model.addAttribute("orders",listOrderByCustomerId);

        return "myOrder";

    }

    @PostMapping("/order/{id}/received")
    public String recivedOrder(@PathVariable("id") String orderId){
        System.out.println("orderId"+orderId);
        ordersService.updateStatus(orderId, "Delivered");
        System.out.println(ordersService.getOrderById(orderId).toString());
        return "redirect:/user/myOrder";
    }



}
