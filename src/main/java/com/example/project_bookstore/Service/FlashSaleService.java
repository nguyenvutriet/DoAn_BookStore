package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.Books;
import com.example.project_bookstore.Entity.FlashSale;
import com.example.project_bookstore.Entity.FlashSaleDetail;
import com.example.project_bookstore.Entity.FlashSaleDetailId;
import com.example.project_bookstore.Repository.FlashSaleRepository;
import com.example.project_bookstore.Repository.FlashSaleDetailRepository;
import com.example.project_bookstore.Repository.IBooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlashSaleService {

    @Autowired
    private FlashSaleRepository flashSaleRepository;
    @Autowired
    private FlashSaleDetailRepository flashSaleDetailRepository;
    @Autowired
    private IBooksRepository booksRepository;

    public Optional<FlashSale> getCurrentActive() {
        List<FlashSale> activeList = flashSaleRepository.findCurrentActiveList(LocalDateTime.now());
        if (activeList.isEmpty()) {
            return Optional.empty();
        }
        // Nếu lỡ có nhiều đợt active chồng thời gian, ưu tiên đợt có startTime gần nhất (mới nhất)
        return Optional.of(activeList.get(0));
    }

    public Optional<FlashSaleDetail> getActiveSaleForBook(String bookId) {
        List<FlashSaleDetail> list = flashSaleDetailRepository.findActiveByBookIdList(bookId, LocalDateTime.now());
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public Map<String, FlashSaleDetail> getActiveSaleMapForBooks(List<String> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) return new HashMap<>();
        List<FlashSaleDetail> details = flashSaleDetailRepository.findActiveByBookIds(bookIds, LocalDateTime.now());
        Map<String, FlashSaleDetail> map = new HashMap<>();
        for (FlashSaleDetail d : details) {
            map.put(d.getBook().getBookId(), d);
        }
        return map;
    }

    public String generateId() {
        List<FlashSale> flashSales = flashSaleRepository.findAll();
        int max = 0;
        for (FlashSale flashSale : flashSales) {
            String id = flashSale.getFlashSaleId();
            if (id != null && id.startsWith("FS")) {
                try {
                    int value = Integer.parseInt(id.substring(2));
                    if (value > max) {
                        max = value;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return String.format("FS%04d", max + 1);
    }

    @Transactional
    public void deleteFlashSale(String flashSaleId) {
        flashSaleRepository.deleteById(flashSaleId);
    }

    @Transactional
    public int addBooksByCategory(String flashSaleId, List<String> categoryIds,
                                  BigDecimal discountPercent, Integer quantityLimit) {
        if (discountPercent == null) {
            throw new IllegalArgumentException("Phần trăm giảm giá không được để trống");
        }
        if (discountPercent.compareTo(BigDecimal.ONE) < 0
                || discountPercent.compareTo(BigDecimal.valueOf(99)) > 0) {
            throw new IllegalArgumentException("Phần trăm giảm giá phải trong khoảng 1-99");
        }
        if (quantityLimit == null || quantityLimit <= 0) {
            throw new IllegalArgumentException("quantityLimit phải lớn hơn 0");
        }
        if (categoryIds == null || categoryIds.isEmpty()) {
            return 0;
        }

        FlashSale flashSale = flashSaleRepository.findById(flashSaleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Flash Sale"));

        List<Books> books = booksRepository.findByCategory_CategoryIdIn(categoryIds);
        if (books.isEmpty()) {
            return 0;
        }

        List<String> bookIds = books.stream().map(Books::getBookId).toList();
        Map<String, FlashSaleDetail> existingMap = flashSaleDetailRepository
                .findByFlashSaleIdAndBookIds(flashSaleId, bookIds)
                .stream()
                .collect(Collectors.toMap(d -> d.getBook().getBookId(), d -> d));

        List<FlashSaleDetail> toSave = new ArrayList<>();
        BigDecimal multiplier = BigDecimal.ONE.subtract(
                discountPercent.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
        );

        for (Books book : books) {
            BigDecimal salePrice = book.getPrice().multiply(multiplier)
                    .setScale(-3, RoundingMode.DOWN);
            if (salePrice.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            FlashSaleDetail detail = existingMap.get(book.getBookId());
            if (detail == null) {
                detail = new FlashSaleDetail();
                detail.setFlashSale(flashSale);
                detail.setBook(book);
                detail.setQuantitySold(0);
            }
            detail.setOriginalPrice(book.getPrice());
            detail.setSalePrice(salePrice);
            detail.setQuantityLimit(quantityLimit);
            toSave.add(detail);
        }

        flashSaleDetailRepository.saveAll(toSave);
        return toSave.size();
    }

    @Transactional
    public void addBookToFlashSale(String flashSaleId, String bookId,
                                   BigDecimal salePrice, Integer quantityLimit) {
        if (salePrice == null || salePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("salePrice phải lớn hơn 0");
        }
        if (quantityLimit == null || quantityLimit <= 0) {
            throw new IllegalArgumentException("quantityLimit phải lớn hơn 0");
        }

        FlashSaleDetailId id = new FlashSaleDetailId(flashSaleId, bookId);
        FlashSaleDetail detail = flashSaleDetailRepository.findById(id).orElse(null);

        if (detail == null) {
            FlashSale flashSale = flashSaleRepository.getReferenceById(flashSaleId);
            Books book = booksRepository.getReferenceById(bookId);

            detail = new FlashSaleDetail();
            detail.setFlashSale(flashSale);
            detail.setBook(book);
            detail.setQuantitySold(0);
            detail.setOriginalPrice(book.getPrice());
        }

        detail.setSalePrice(salePrice);
        detail.setQuantityLimit(quantityLimit);
        flashSaleDetailRepository.save(detail);
    }

    @Transactional
    public void removeBookFromFlashSale(String flashSaleId, String bookId) {
        FlashSaleDetail detail = flashSaleDetailRepository
                .findByFlashSale_FlashSaleIdAndBook_BookId(flashSaleId, bookId)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy chi tiết flash sale"));
        flashSaleDetailRepository.delete(detail);
    }

    public FlashSaleDetailPageData getFlashSaleDetailPageData(String flashSaleId) {
        FlashSale flashSale = flashSaleRepository.findById(flashSaleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Flash Sale"));
        List<FlashSaleDetail> details = flashSaleDetailRepository.findByFlashSaleIdWithBook(flashSaleId);
        return new FlashSaleDetailPageData(flashSale, details);
    }

    public List<FlashSaleListItem> getAllFlashSalesWithCount() {
        List<FlashSale> all = flashSaleRepository.findAll();
        Map<String, Long> countMap = flashSaleDetailRepository.countDetailsGroupByFlashSale()
                .stream()
                .collect(Collectors.toMap(r -> (String) r[0], r -> (Long) r[1]));

        List<FlashSaleListItem> items = new ArrayList<>();
        for (FlashSale flashSale : all) {
            items.add(new FlashSaleListItem(flashSale, countMap.getOrDefault(flashSale.getFlashSaleId(), 0L)));
        }
        return items;
    }

    @Retryable(value = OptimisticLockingFailureException.class, maxAttempts = 3)
    @Transactional
    public void reserveStock(String flashSaleId, String bookId, int qty) {
        FlashSaleDetail item = flashSaleDetailRepository
                .findByFlashSale_FlashSaleIdAndBook_BookId(flashSaleId, bookId)
                .orElseThrow(() -> new IllegalStateException("Sách không thuộc chương trình flash sale này"));

        if (item.getQuantitySold() + qty > item.getQuantityLimit()) {
            throw new IllegalStateException("Flash sale đã hết hàng cho sản phẩm này");
        }
        item.setQuantitySold(item.getQuantitySold() + qty);
        flashSaleDetailRepository.save(item);
    }

    public static class FlashSaleDetailPageData {
        private final FlashSale flashSale;
        private final List<FlashSaleDetail> details;

        public FlashSaleDetailPageData(FlashSale flashSale, List<FlashSaleDetail> details) {
            this.flashSale = flashSale;
            this.details = details;
        }

        public FlashSale getFlashSale() {
            return flashSale;
        }

        public List<FlashSaleDetail> getDetails() {
            return details;
        }
    }

    public static class FlashSaleListItem {
        private final FlashSale flashSale;
        private final long bookCount;

        public FlashSaleListItem(FlashSale flashSale, long bookCount) {
            this.flashSale = flashSale;
            this.bookCount = bookCount;
        }

        public FlashSale getFlashSale() {
            return flashSale;
        }

        public long getBookCount() {
            return bookCount;
        }
    }

    @Transactional
    public FlashSale saveOrUpdateFlashSale(String flashSaleId, String name, String description,
                                           LocalDateTime startTime, LocalDateTime endTime, String status) {

        FlashSale flashSale;

        if (flashSaleId != null && !flashSaleId.isBlank() && flashSaleRepository.existsById(flashSaleId)) {
            flashSale = flashSaleRepository.findById(flashSaleId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Flash Sale"));
        } else {
            flashSale = new FlashSale();
            flashSale.setFlashSaleId(
                    (flashSaleId != null && !flashSaleId.isBlank()) ? flashSaleId : generateId()
            );
        }

        // ===== Cảnh báo nếu đang tạo/sửa thành 1 đợt ACTIVE chồng thời gian với đợt khác =====
        if ("ACTIVE".equals(status)) {
            List<FlashSale> overlapping = flashSaleRepository.findAll().stream()
                    .filter(fs -> !fs.getFlashSaleId().equals(flashSale.getFlashSaleId()))
                    .filter(fs -> "ACTIVE".equals(fs.getStatus()))
                    .filter(fs -> startTime.isBefore(fs.getEndTime()) && endTime.isAfter(fs.getStartTime()))
                    .toList();

            if (!overlapping.isEmpty()) {
                throw new IllegalArgumentException(
                        "Khoảng thời gian này trùng với đợt Flash Sale đang ACTIVE khác: "
                                + overlapping.get(0).getName()
                                + ". Vui lòng chọn thời gian khác hoặc tắt đợt kia trước."
                );
            }
        }

        flashSale.setName(name);
        flashSale.setDescription(description);
        flashSale.setStartTime(startTime);
        flashSale.setEndTime(endTime);
        flashSale.setStatus(status);

        return flashSaleRepository.save(flashSale);
    }
}