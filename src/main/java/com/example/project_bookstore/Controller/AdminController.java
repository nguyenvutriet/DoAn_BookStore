package com.example.project_bookstore.Controller;


import com.example.project_bookstore.Entity.Books;
import com.example.project_bookstore.Entity.Category;
import com.example.project_bookstore.Entity.Users;
import com.example.project_bookstore.Repository.IUsersRepository;
import com.example.project_bookstore.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    // CATEGORY
    @GetMapping("/categories")
    public String categoryList(Model model) {
        List<Category> categories = adminService.getAllCategories();

        model.addAttribute("categories", categories);

        Map<String, Long> totalBooksMap = new HashMap<>();
        Map<String, Long> soldBooksMap = new HashMap<>();

        for (Category c : categories) {
            totalBooksMap.put(c.getCategoryId(), adminService.getTotalBooksOfCategory(c.getCategoryId()));
            soldBooksMap.put(c.getCategoryId(), adminService.getSoldBooksOfCategory(c.getCategoryId()));
        }

        model.addAttribute("totalBooksMap", totalBooksMap);
        model.addAttribute("soldBooksMap", soldBooksMap);

        return "admin-category-list";
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model) {
        model.addAttribute("category", new Category());
        return "admin-category-form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category) {
        adminService.saveCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable String id, Model model) {

        boolean deleted = adminService.deleteCategory(id);

        if (!deleted) {

            List<Category> categories = adminService.getAllCategories();
            model.addAttribute("categories", categories);

            Map<String, Long> totalBooksMap = new HashMap<>();
            Map<String, Long> soldBooksMap = new HashMap<>();

            for (Category c : categories) {
                totalBooksMap.put(c.getCategoryId(), adminService.getTotalBooksOfCategory(c.getCategoryId()));
                soldBooksMap.put(c.getCategoryId(), adminService.getSoldBooksOfCategory(c.getCategoryId()));
            }

            model.addAttribute("totalBooksMap", totalBooksMap);
            model.addAttribute("soldBooksMap", soldBooksMap);

            model.addAttribute("errorMessage", "Không thể xóa vì thể loại vẫn còn sách!");

            return "admin-category-list";
        }


        return "redirect:/admin/categories";
    }


    @GetMapping("/categories/{id}")
    public String viewCategory(@PathVariable String id, Model model) {
        model.addAttribute("category", adminService.getCategory(id));
        model.addAttribute("totalBooks", adminService.getTotalBooksOfCategory(id));
        model.addAttribute("soldBooks", adminService.getSoldBooksOfCategory(id));
        return "admin-category-detail";
    }

    // ============================= BOOKS =============================
    @GetMapping("/books")
    public String listBooks(Model model) {
        model.addAttribute("books", adminService.getAllBooks());
        return "admin-book-list";
    }

    @GetMapping("/books/{id}")
    public String viewBook(@PathVariable String id, Model model) {
        model.addAttribute("book", adminService.getBook(id));
        return "admin-book-detail";
    }

    @GetMapping("/books/new")
    public String newBook(Model model) {
        model.addAttribute("book", new Books());
        model.addAttribute("categories", adminService.getAllCategories());
        return "admin-book-form";
    }

    @PostMapping("/books/save")
    public String saveBook(@ModelAttribute Books book,
                           @RequestParam("imageFile") MultipartFile file) {

        try {
            if (!file.isEmpty()) {
                String fileName = file.getOriginalFilename();
                Path uploadDir = Paths.get("src/main/resources/static/images/");

                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                Path path = uploadDir.resolve(fileName);
                Files.write(path, file.getBytes());

                book.setPicture("/images/" + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        adminService.saveBook(book);
        return "redirect:/admin/books";
    }


    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable String id, Model model) {

        String result = adminService.deleteBookSafe(id);

        if (!result.equals("SUCCESS")) {
            model.addAttribute("error", result);
            model.addAttribute("books", adminService.getAllBooks());  // load lại danh sách
            return "admin-book-list";
        }

        return "redirect:/admin/books";
    }

    //CUS
    @GetMapping("/customers")
    public String listCustomers(Model model) {
        model.addAttribute("customers", adminService.getAllCustomers());
        return "admin-customer-list";
    }

    @GetMapping("/customers/{id}")
    public String viewCustomer(@PathVariable String id, Model model) {

        model.addAttribute("customer", adminService.getCustomer(id));

        return "admin-customer-detail";
    }

    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable String id) {
        adminService.deleteCustomer(id);
        return "redirect:/admin/customers";
    }

    //ORDERS
    @GetMapping("/orders")
    public String listOrders(Model model) {
        model.addAttribute("orders", adminService.getAllOrders());
        return "admin-order-list";
    }

    @GetMapping("/orders/{id}")
    public String viewOrder(@PathVariable String id, Model model) {

        model.addAttribute("order", adminService.getOrder(id));
        model.addAttribute("details", adminService.getOrderDetails(id));

        return "admin-order-detail";
    }

    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable String id) {
        adminService.deleteOrder(id);
        return "redirect:/admin/orders";
    }

    //review
    @GetMapping("/reviews")
    public String listReviews(Model model) {
        model.addAttribute("reviews", adminService.getAllReviews());
        return "admin-review-list";
    }

    @GetMapping("/reviews/{id}")
    public String viewReview(@PathVariable String id, Model model) {

        model.addAttribute("review", adminService.getReview(id));

        return "admin-review-detail";
    }

    @GetMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable String id) {
        adminService.deleteReview(id);
        return "redirect:/admin/reviews";
    }

}