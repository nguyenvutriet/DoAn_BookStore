package com.example.project_bookstore.Controller;


import com.example.project_bookstore.Entity.*;
import com.example.project_bookstore.Repository.IBooksRepository;
import com.example.project_bookstore.Repository.ICategoryRepository;
import com.example.project_bookstore.Repository.ICustomersRepository;
import com.example.project_bookstore.Repository.IUsersRepository;
import com.example.project_bookstore.Service.AdminService;
import com.example.project_bookstore.Service.BooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    @Autowired
    private IBooksRepository booksRepository;

    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private ICustomersRepository customersRepository;

    @Autowired
    private BooksService bookService;

    // DASHBOARD
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("totalUsers", adminService.getTotalUsers());
        model.addAttribute("totalBooks", adminService.getTotalBooks());
        model.addAttribute("totalCustomers", adminService.getTotalCustomers());
        model.addAttribute("totalOrders", adminService.getTotalOrders());
        model.addAttribute("totalReviews", adminService.getTotalReviews());

        // Biểu đồ doanh thu 6 tháng gần nhất
        var monthly = adminService.getMonthlyRevenue("6M");
        model.addAttribute("months", monthly.get("labels"));
        model.addAttribute("monthlyRevenue", monthly.get("values"));

        model.addAttribute("recentBooks", adminService.getRecentSoldBooks());

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
    public String saveCategory(Category category, Model model) {

        boolean idExists = adminService.existsCategoryId(category.getCategoryId());
        boolean nameExists = adminService.existsCategoryName(category.getCategoryName());

        if (idExists || nameExists) {
            model.addAttribute("category", category);

            if (idExists) {
                model.addAttribute("errorMessage", "Category ID already exists!");
            }
            if (nameExists) {
                model.addAttribute("errorMessage", "Category name already exists!");
            }

            return "admin-category-form";
        }

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

        // 1. Thông tin thể loại
        model.addAttribute("category", adminService.getCategory(id));
        model.addAttribute("totalBooks", adminService.getTotalBooksOfCategory(id));
        model.addAttribute("soldBooks", adminService.getSoldBooksOfCategory(id));

        // 2. Sách còn hàng
        model.addAttribute("inStockBooks", adminService.getInStockBooks(id));

        // 3. Sách hết hàng
        model.addAttribute("outOfStockBooks", adminService.getOutOfStockBooks(id));

        return "admin-category-detail";
    }


    //BOOKS
    @GetMapping("books")
    public String booksByCategory(
            @RequestParam(required = false) String categoryId,
            Model model
    ) {
        List<Category> categories = adminService.getAllCategories();
        model.addAttribute("categories", categories);

        if (categoryId == null && !categories.isEmpty()) {
            categoryId = categories.get(0).getCategoryId();
        }

        model.addAttribute("selectedCategory", categoryId);
        model.addAttribute("inStockBooks", adminService.getInStockBooks(categoryId));
        model.addAttribute("outOfStockBooks", adminService.getOutOfStockBooks(categoryId));

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
                           @RequestParam("imageFile") MultipartFile imageFile,
                           Model model) {

        // ========== 1. Kiểm tra mã sách trùng ==========
        boolean isNew = booksRepository.findById(book.getBookId()).isEmpty();

        if (isNew && booksRepository.existsByBookId(book.getBookId())) {
            model.addAttribute("errorMessage", "❗ Mã sách đã tồn tại!");
            model.addAttribute("book", book);
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin-book-form";
        }


        // ========== 2. Kiểm tra số lượng tồn > 0 ==========
        if (book.getQuantity() <= 0) {
            model.addAttribute("errorMessage", "❗ Số lượng tồn phải lớn hơn 0!");
            model.addAttribute("book", book);
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin-book-form";
        }

        // ========== 3. Upload ảnh nếu có ==========
        try {
            if (!imageFile.isEmpty()) {

                String fileName = imageFile.getOriginalFilename();

                Path uploadPath = Paths.get("src/main/resources/static/images/books");

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Files.copy(
                        imageFile.getInputStream(),
                        uploadPath.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING
                );

                book.setPicture(fileName);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // ========== 4. Lưu sách ==========
        booksRepository.save(book);

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

        Customers customer = adminService.getCustomer(id);
        model.addAttribute("customer", customer);

        model.addAttribute("orders", adminService.getOrdersByCustomer(id));

        return "admin-customer-detail";
    }

    //ORDERS
    @GetMapping("/orders")
    public String listOrders(
            @RequestParam(defaultValue = "Pending") String status,
            Model model
    ) {
        List<Orders> orders = adminService.getOrdersByStatus(status);

        model.addAttribute("status", status);
        model.addAttribute("orders", orders);

        return "admin-order-list";
    }

    @GetMapping("/orders/{id}")
    public String viewOrder(@PathVariable String id, Model model) {

        model.addAttribute("order", adminService.getOrder(id));
        model.addAttribute("details", adminService.getOrderDetails(id));

        return "admin-order-detail";
    }

    @GetMapping("/orders/confirm/{id}")
    public String confirmOrder(@PathVariable String id) {

        Orders order = adminService.getOrder(id);

        if (order != null && order.getStatus().equals("Pending")) {
            order.setStatus("Confirmed");
            adminService.saveOrder(order);
        }

        return "redirect:/admin/orders?status=Pending";
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

    //Doanh thu
    @GetMapping("/revenue")
    public String revenuePage(@RequestParam(defaultValue = "YEAR") String filter, Model model) {

        model.addAttribute("filter", filter);

        model.addAttribute("totalRevenue", adminService.getTotalRevenue());
        model.addAttribute("totalOrders", adminService.getTotalDeliveredOrders());

        var monthly = adminService.getMonthlyRevenue(filter);
        model.addAttribute("months", monthly.get("labels"));
        model.addAttribute("monthlyRevenue", monthly.get("values"));
        model.addAttribute("growth", monthly.get("growth"));

        var cat = adminService.getRevenueByCategory();
        model.addAttribute("categoryLabels", cat.get("labels"));
        model.addAttribute("categoryRevenue", cat.get("values"));

        return "admin-revenue";
    }
}