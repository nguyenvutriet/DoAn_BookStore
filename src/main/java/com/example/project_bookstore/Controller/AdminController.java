package com.example.project_bookstore.Controller;


import com.example.project_bookstore.Entity.*;
import com.example.project_bookstore.Repository.IBooksRepository;
import com.example.project_bookstore.Repository.ICategoryRepository;
import com.example.project_bookstore.Repository.ICustomersRepository;
import com.example.project_bookstore.Repository.IUsersRepository;
import com.example.project_bookstore.Service.*;
import com.example.project_bookstore.Service.BooksService;
import com.example.project_bookstore.Service.PredictService;
import com.example.project_bookstore.Service.UsersService;
import com.example.project_bookstore.Service.BooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
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

    @Autowired
    private UsersService userService;

    @Autowired
    private PredictService predictService;

    @Autowired
    private PythonService pythonService;
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
    public String viewUser(@PathVariable("userName") String userName, Model model) {
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
    public String deleteCategory(@PathVariable("id") String id, Model model) {

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
    public String viewCategory(@PathVariable("id") String id, Model model) {

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


    //    @GetMapping("/books/{id}")
//    public String viewBook(@PathVariable String id, Model model) {
//        model.addAttribute("book", adminService.getBook(id));
//        return "admin-book-detail";
//    }
    @GetMapping("/books/{id}")
    public String viewBook(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "false") boolean edit,
            Model model
    ) {
        model.addAttribute("book", adminService.getBook(id));
        model.addAttribute("categories", adminService.getAllCategories());
        model.addAttribute("edit", edit);
        return "admin-book-detail";
    }


    @GetMapping("/books/new")
    public String newBook(Model model) {
        model.addAttribute("book", new Books());
        model.addAttribute("categories", adminService.getAllCategories());
        return "admin-book-form";
    }

    @PostMapping("/books/save")
    public String saveBook(@ModelAttribute Books formBook,
                           @RequestParam("imageFile") MultipartFile imageFile,
                           RedirectAttributes redirectAttributes,
                           Model model) {

        boolean isNew = !booksRepository.existsById(formBook.getBookId());

        Books book;

        if (isNew) {
            book = formBook;

            if (book.getTitle() == null || book.getTitle().isBlank()) {
                model.addAttribute("error", "Tên sách không được để trống");
                model.addAttribute("book", book);
                model.addAttribute("categories", categoryRepository.findAll());
                return "admin-book-form";
            }

            if (book.getQuantity() < 0) {
                book.setQuantity(0);
            }

        } else {
            book = booksRepository.findById(formBook.getBookId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sách"));

            if (formBook.getTitle() != null) {
                book.setTitle(formBook.getTitle());
            }

            book.setAuthor(formBook.getAuthor());
            book.setPublisher(formBook.getPublisher());
            book.setPublicationYear(formBook.getPublicationYear());
            book.setPrice(formBook.getPrice());
            book.setDescription(formBook.getDescription());
            book.setCategory(formBook.getCategory());

        }

        // ===== UPLOAD ẢNH (CHUNG CHO ADD + EDIT) =====
        try {
            if (!imageFile.isEmpty()) {
                String fileName = imageFile.getOriginalFilename();

                Path sourcePath = Paths.get("src/main/resources/static/images/books");

                Path runtimePath = Paths.get("target/classes/static/images/books");

                Files.createDirectories(sourcePath);
                Files.createDirectories(runtimePath);

                Files.copy(
                        imageFile.getInputStream(),
                        sourcePath.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING
                );

                Files.copy(
                        imageFile.getInputStream(),
                        runtimePath.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING
                );

                book.setPicture(fileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isNew && book.getPicture() == null) {
            model.addAttribute("error", "Vui lòng chọn ảnh bìa");
            model.addAttribute("book", book);
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin-book-form";
        }

        booksRepository.save(book);

        if (isNew) {
            redirectAttributes.addFlashAttribute("success", "➕ Thêm sách thành công");
            return "redirect:/admin/books";
        } else {
            redirectAttributes.addFlashAttribute("success", "💾 Cập nhật sách thành công");
            return "redirect:/admin/books/" + book.getBookId();
        }
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
        Users us = userService.getUserByCustomerId(customer.getCustomerId());
        model.addAttribute("customer", customer);
        model.addAttribute("us", us);
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

        Orders order = adminService.getOrder(id);
        List<OrderDetail> details = adminService.getOrderDetails(id);

        // tính tổng tiền sách
        BigDecimal totalBooks = details.stream()
                .map(d -> d.getUnitPrice().multiply(BigDecimal.valueOf(d.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // phí ship = tổng thanh toán - tiền sách
        BigDecimal shipFee = order.getTotalAmount().subtract(totalBooks);

        model.addAttribute("order", order);
        model.addAttribute("details", details);
        model.addAttribute("shipFee", shipFee);

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

        model.addAttribute("currentMonthRevenue", adminService.getCurrentMonthRevenue());
        model.addAttribute("currentMonthOrders", adminService.getCurrentMonthOrders());
        model.addAttribute("growthRate", adminService.getCurrentMonthGrowthRate());

        model.addAttribute("aov", adminService.getAverageOrderValueThisMonth());

        model.addAttribute("topBooks", adminService.getTop8BestSellingBooks());

        return "admin-revenue";
    }

    @GetMapping("/revenue/predict")
    @ResponseBody
    public String runPythonAPI(){
        File file = predictService.exportToCSV();
        return predictService.runPredictModel();
    }


    @PostMapping("/books/update-qty")
    public String updateQuantity(
            @RequestParam String bookId,
            @RequestParam(value = "quantity", required = false) Integer quantity
    ) {
        Books book = booksRepository.findById(bookId).orElseThrow();

        int addQty = (quantity == null) ? 0 : quantity;

        book.setQuantity(book.getQuantity() + addQty);
        booksRepository.save(book);

        return "redirect:/admin/books";
    }
    @Autowired
    private ChatService chatService;

    // Trang chat admin
    @GetMapping("/chat")
    public String adminChatPage(Model model) {
        model.addAttribute("users", chatService.allChatUsers());
        return "dashboard-chat";
    }

    // Lấy lịch sử + MARK SEEN
    @GetMapping("/chat/{userName}")
    @ResponseBody
    public List<ChatMessage> getUserMessages(@PathVariable String userName) {
        chatService.markSeen(userName); // 🔥 đánh dấu đã đọc
        return chatService.history(userName);
    }

    // Danh sách user (đã sort)
    @GetMapping("/chat/users")
    @ResponseBody
    public List<String> getChatUsers() {
        return chatService.allChatUsers();
    }

    // 🔴 UNREAD COUNT
    @GetMapping("/chat/unread/{userName}")
    @ResponseBody
    public long unread(@PathVariable String userName) {
        return chatService.unreadCount(userName);
    }

}