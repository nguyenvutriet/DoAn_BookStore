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
import java.time.LocalDate;
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
    public String updateCustomer(@RequestParam("customerId") String customerId, @RequestParam("fullName") String fullName, @RequestParam("phone") String phone, @RequestParam("email") String email, @RequestParam("address") String address, @RequestParam("birthDate") String birthDate, RedirectAttributes redirect){

        if(fullName.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty() || birthDate.isEmpty()){
            redirect.addFlashAttribute("error", "Vui lòng điền đầy đủ thông tin");
            return "redirect:/user/profile";
        }

        if(!phone.matches("^(0|\\+84)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-5]|9[0-4|6-9])[0-9]{7}$")){
            redirect.addFlashAttribute("error", "Số điện thoại không hợp lệ");
            return "redirect:/user/profile";
        }

        if(phone.length() < 10 || phone.length() > 11){
            redirect.addFlashAttribute("error", "Số điện thoại không hợp lệ");
            return "redirect:/user/profile";
        }

        LocalDate birth = LocalDate.parse(birthDate);
        LocalDate today = LocalDate.now();

        if (birth.isAfter(today)) {
            redirect.addFlashAttribute("error",
                    "Ngày sinh không được vượt quá ngày hiện tại");
            return "redirect:/user/profile";
        }

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
        return "redirect:/user/profile";
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
        ordersService.updateStatus(orderId, "Delivered");
        return "redirect:/user/myOrder";
    }

    @PostMapping("/order/{id}/canceled")
    public String canceledOrder(@PathVariable("id") String orderId){
        ordersService.updateStatus(orderId, "Cancelled");
        return "redirect:/user/myOrder";
    }

    @GetMapping("/filter")
    public String filterOrder(@AuthenticationPrincipal UserDetails userDetails, Model model, @RequestParam("status") String status){
        String username =   userDetails.getUsername();
        Users user = usersService.getUserByUserName(username);
        Customers cus = customersService.getCustomerById(user.getCustomer().getCustomerId());

        List<Orders> listOrderByCustomerId;
        if(status.equals("All")){
            listOrderByCustomerId = ordersService.getOrders(cus.getCustomerId());
        } else {
            listOrderByCustomerId = ordersService.getOrdersByStatus(cus.getCustomerId(), status);
        }

        model.addAttribute("orders",listOrderByCustomerId);
        model.addAttribute("selectedStatus", status);
        return "myOrder";
    }

}
