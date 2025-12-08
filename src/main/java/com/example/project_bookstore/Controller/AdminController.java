package com.example.project_bookstore.Controller;


import com.example.project_bookstore.Entity.Users;
import com.example.project_bookstore.Repository.IUsersRepository;
import com.example.project_bookstore.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private IUsersRepository usersRepository;

    // DASHBOARD
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("totalUsers", adminService.getTotalUsers());
        model.addAttribute("totalBooks", adminService.getTotalBooks());
        model.addAttribute("totalCustomers", adminService.getTotalCustomers());
        model.addAttribute("totalOrders", adminService.getTotalOrders());
        model.addAttribute("totalReviews", adminService.getTotalReviews());

        return "admin-dashboard";
    }

    //USERS
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", usersRepository.findAll());
        return "admin-users";
    }

    @GetMapping("/users/{userName}")
    public String viewUser(@PathVariable String userName, Model model) {
        Users user = usersRepository.findById(userName).orElse(null);
        model.addAttribute("user", user);
        return "admin-user-detail";
    }

    @GetMapping("/users/delete/{userName}")
    public String deleteUser(@PathVariable String userName) {
        usersRepository.deleteById(userName);
        return "redirect:/admin/users";
    }
}