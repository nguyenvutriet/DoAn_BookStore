package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Entity.Books;
import com.example.project_bookstore.Entity.Category;
import com.example.project_bookstore.Entity.FlashSale;
import com.example.project_bookstore.Repository.IBooksRepository;
import com.example.project_bookstore.Repository.ICategoryRepository;
import com.example.project_bookstore.Service.FlashSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/flash-sale")
public class AdminFlashSaleController {

    @Autowired
    private FlashSaleService flashSaleService;

    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private IBooksRepository booksRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("flashSaleItems", flashSaleService.getAllFlashSalesWithCount());
        model.addAttribute("currentFlashSale", flashSaleService.getCurrentActive().orElse(null));
        return "admin-flash-sale-list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        FlashSale flashSale = new FlashSale();
        flashSale.setFlashSaleId(flashSaleService.generateId());
        flashSale.setStatus("ACTIVE");
        model.addAttribute("flashSale", flashSale);
        model.addAttribute("details", java.util.Collections.emptyList());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("books", booksRepository.findAll());
        return "admin-flash-sale-form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) String flashSaleId,
                       @RequestParam String name,
                       @RequestParam(required = false) String description,
                       @RequestParam String startTime,
                       @RequestParam String endTime,
                       @RequestParam(required = false, defaultValue = "ACTIVE") String status,
                       @org.springframework.web.bind.annotation.RequestHeader(value = "referer", required = false) String referer,
                       RedirectAttributes redirectAttributes) {

        java.time.LocalDateTime parsedStart;
        java.time.LocalDateTime parsedEnd;

        try {
            parsedStart = java.time.LocalDateTime.parse(startTime);
            parsedEnd = java.time.LocalDateTime.parse(endTime);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Thời gian không hợp lệ");
            if (referer != null && referer.contains("/flash-sale/new")) {
                return "redirect:/admin/flash-sale/new";
            }
            return "redirect:/admin/flash-sale/" + flashSaleId;
        }

        if (parsedEnd.isBefore(parsedStart)) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Thời gian kết thúc phải sau thời gian bắt đầu");
            if (referer != null && referer.contains("/flash-sale/new")) {
                return "redirect:/admin/flash-sale/new";
            }
            return "redirect:/admin/flash-sale/" + flashSaleId;
        }

        try {
            FlashSale saved = flashSaleService.saveOrUpdateFlashSale(
                    flashSaleId, name, description, parsedStart, parsedEnd, status);

            redirectAttributes.addFlashAttribute("successMessage", "✅ Đã lưu Flash Sale");
            return "redirect:/admin/flash-sale/" + saved.getFlashSaleId();

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());

            // Kiểm tra xem có phải đang thao tác trên form tạo mới hay không dựa vào URL Referer
            if (referer != null && referer.contains("/flash-sale/new")) {
                return "redirect:/admin/flash-sale/new";
            }
            return "redirect:/admin/flash-sale/" + flashSaleId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Lưu thất bại: " + e.getMessage());
            if (referer != null && referer.contains("/flash-sale/new")) {
                return "redirect:/admin/flash-sale/new";
            }
            return "redirect:/admin/flash-sale/" + flashSaleId;
        }
    }

    @GetMapping("/{id}")
    public String editFlashSale(@PathVariable String id, Model model,
                                RedirectAttributes redirectAttributes) {

        // Chặn id rỗng hoặc chuỗi "null" lọt vào
        if (id == null || id.isBlank() || "null".equalsIgnoreCase(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Mã Flash Sale không hợp lệ");
            return "redirect:/admin/flash-sale";
        }

        try {
            var pageData = flashSaleService.getFlashSaleDetailPageData(id);
            model.addAttribute("flashSale", pageData.getFlashSale());
            model.addAttribute("details", pageData.getDetails());
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("books", booksRepository.findAll());
            return "admin-flash-sale-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Không tìm thấy Flash Sale với mã: " + id);
            return "redirect:/admin/flash-sale";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteFlashSale(@PathVariable String id, RedirectAttributes redirectAttributes) {

        if (id == null || id.isBlank() || "null".equalsIgnoreCase(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Mã Flash Sale không hợp lệ");
            return "redirect:/admin/flash-sale";
        }

        try {
            flashSaleService.deleteFlashSale(id);
            redirectAttributes.addFlashAttribute("successMessage", "🗑️ Đã xoá Flash Sale");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Xoá thất bại: " + e.getMessage());
        }

        return "redirect:/admin/flash-sale";
    }

    @PostMapping("/{id}/add-category")
    public String addByCategory(@PathVariable String id,
                                @RequestParam(required = false) List<String> categoryIds,
                                @RequestParam(required = false) String discountPercent,
                                @RequestParam(required = false) String quantityLimit,
                                RedirectAttributes redirectAttributes) {

        if (id == null || id.isBlank() || "null".equalsIgnoreCase(id)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "❌ Vui lòng lưu thông tin cơ bản của Flash Sale trước khi áp dụng danh mục.");
            return "redirect:/admin/flash-sale";
        }

        if (categoryIds == null || categoryIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Vui lòng chọn ít nhất 1 danh mục");
            return "redirect:/admin/flash-sale/" + id;
        }

        BigDecimal parsedPercent;
        Integer parsedLimit;

        try {
            if (discountPercent == null || discountPercent.isBlank()) {
                throw new NumberFormatException();
            }
            parsedPercent = new BigDecimal(discountPercent.trim());
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Phần trăm giảm giá không hợp lệ");
            return "redirect:/admin/flash-sale/" + id;
        }

        try {
            if (quantityLimit == null || quantityLimit.isBlank()) {
                throw new NumberFormatException();
            }
            parsedLimit = Integer.parseInt(quantityLimit.trim());
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Số lượng giới hạn không hợp lệ");
            return "redirect:/admin/flash-sale/" + id;
        }

        try {
            int added = flashSaleService.addBooksByCategory(id, categoryIds, parsedPercent, parsedLimit);
            if (added == 0) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "⚠️ Không có sách nào thuộc danh mục đã chọn để áp dụng");
            } else {
                redirectAttributes.addFlashAttribute("successMessage", "✅ Đã áp dụng cho " + added + " sách");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Không tìm thấy Flash Sale với mã: " + id);
        }

        return "redirect:/admin/flash-sale/" + id;
    }

    @PostMapping("/{id}/add-book")
    public String addSingleBook(@PathVariable String id,
                                @RequestParam(required = false) String bookId,
                                @RequestParam(required = false) BigDecimal salePrice,
                                @RequestParam(required = false) Integer quantityLimit,
                                RedirectAttributes redirectAttributes) {

        // ===== Chặn khi chưa tạo Flash Sale =====
        if (id == null || id.isBlank() || "null".equalsIgnoreCase(id)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "❌ Vui lòng lưu thông tin cơ bản của Flash Sale trước khi thêm sách.");
            return "redirect:/admin/flash-sale";
        }

        if (bookId == null || bookId.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Vui lòng chọn sách");
            return "redirect:/admin/flash-sale/" + id;
        }

        if (salePrice == null || salePrice.compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Giá sale không hợp lệ");
            return "redirect:/admin/flash-sale/" + id;
        }

        if (quantityLimit == null || quantityLimit < 1) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Số lượng giới hạn phải lớn hơn 0");
            return "redirect:/admin/flash-sale/" + id;
        }

        try {
            flashSaleService.addBookToFlashSale(id, bookId, salePrice, quantityLimit);
            redirectAttributes.addFlashAttribute("successMessage", "➕ Đã thêm sách vào Flash Sale");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
        }

        return "redirect:/admin/flash-sale/" + id;
    }

    @PostMapping("/{id}/delete-detail")
    public String deleteDetail(@PathVariable String id,
                               @RequestParam String bookId,
                               RedirectAttributes redirectAttributes) {

        if (id == null || id.isBlank() || "null".equalsIgnoreCase(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Mã Flash Sale không hợp lệ");
            return "redirect:/admin/flash-sale";
        }

        try {
            flashSaleService.removeBookFromFlashSale(id, bookId);
            redirectAttributes.addFlashAttribute("successMessage", "🗑️ Đã xoá chi tiết flash sale");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Xoá thất bại: " + e.getMessage());
        }

        return "redirect:/admin/flash-sale/" + id;
    }
}