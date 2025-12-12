package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.*;
import com.example.project_bookstore.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdminService {

    @Autowired
    private IUsersRepository usersRepository;

    @Autowired
    private IBooksRepository booksRepository;

    @Autowired
    private ICustomersRepository customersRepository;

    @Autowired
    private IOrdersRepository ordersRepository;

    @Autowired
    private IOrderDetailRepository orderDetailRepository;

    @Autowired
    private IReviewRepository reviewRepository;

    @Autowired
    private ICategoryRepository categoryRepository;


    //dashboard
    public long getTotalUsers() {
        return usersRepository.count();
    }

    public long getTotalBooks() {
        return booksRepository.count();
    }

    public long getTotalCustomers() {
        return customersRepository.count();
    }

    public long getTotalOrders() {
        return ordersRepository.count();
    }

    public long getTotalReviews() {
        return reviewRepository.count();
    }

    //category
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategory(String id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public Long getTotalBooksOfCategory(String categoryId) {
        return categoryRepository.countBooksByCategory(categoryId);
    }

    public Long getSoldBooksOfCategory(String categoryId) {
        return categoryRepository.countSoldBooksByCategory(categoryId);
    }

    public void saveCategory(Category category) {
        categoryRepository.save(category);
    }

    public boolean deleteCategory(String id) {

        Long totalBooks = categoryRepository.countBooksByCategory(id);

        if (totalBooks != null && totalBooks > 0) {
            return false; // vẫn còn sách, không cho xoá
        }

        categoryRepository.deleteById(id);
        return true;
    }

    public List<Books> getInStockBooks(String categoryId) {
        return booksRepository.findInStockByCategory(categoryId);
    }

    public List<Books> getOutOfStockBooks(String categoryId) {
        return booksRepository.findOutOfStockByCategory(categoryId);
    }

    public boolean existsCategoryId(String id) {
        return categoryRepository.existsById(id);
    }

    public boolean existsCategoryName(String name) {
        return categoryRepository.existsByCategoryNameIgnoreCase(name);
    }

    // books
    public List<Books> getAllBooks() {
        return booksRepository.findAll();
    }

    public Books getBook(String id) {
        return booksRepository.findById(id).orElse(null);
    }

    public void saveBook(Books book) {
        booksRepository.save(book);
    }


    public String deleteBookSafe(String bookId) {
        Books book = booksRepository.findById(bookId).orElse(null);

        if (book == null) return "Không tìm thấy sách.";

        if (book.getQuantity() > 0) {
            return "Không thể xóa sách vì vẫn còn " + book.getQuantity() + " quyển trong kho!";
        }

        try {
            booksRepository.deleteById(bookId);
            return "SUCCESS";
        } catch (Exception e) {
            return "Sách này đang được sử dụng trong đơn hàng hoặc giỏ hàng, không thể xóa!";
        }
    }

    //customer
    public List<Customers> getAllCustomers() {
        return customersRepository.findAll();
    }

    public Customers getCustomer(String id) {
        return customersRepository.findById(id).orElse(null);
    }

    public List<Orders> getOrdersByCustomer(String customerId) {
        return ordersRepository.findByCustomer_CustomerId(customerId);
    }
//    public void deleteCustomer(String id) {
//        customersRepository.deleteById(id);
//    }

    //order
    public List<Orders> getAllOrders() {
        return ordersRepository.findAll();
    }


    public Orders getOrder(String id) {
        return ordersRepository.findById(id).orElse(null);
    }

    public List<OrderDetail> getOrderDetails(String orderId) {
        return orderDetailRepository.findByOrder_OrderId(orderId);
    }

    public List<Orders> getOrdersByStatus(String status) {
        return ordersRepository.findByStatus(status);
    }

    public void saveOrder(Orders order) {
        ordersRepository.save(order);
    }

//    public void deleteOrder(String id) {
//        ordersRepository.deleteById(id);
//    }

    //review

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Review getReview(String id) {
        return reviewRepository.findById(id).orElse(null);
    }

    public void deleteReview(String id) {
        reviewRepository.deleteById(id);
    }

    public Date getFilterDate(String filter) {

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);

        switch (filter) {
            case "YEAR":
                cal.set(year, Calendar.JANUARY, 1);
                return cal.getTime();

            case "6M":
                cal.add(Calendar.MONTH, -5);
                return cal.getTime();

            case "Q1":
                cal.set(year, Calendar.JANUARY, 1);
                return cal.getTime();

            case "Q2":
                cal.set(year, Calendar.APRIL, 1);
                return cal.getTime();

            case "Q3":
                cal.set(year, Calendar.JULY, 1);
                return cal.getTime();

            case "Q4":
                cal.set(year, Calendar.OCTOBER, 1);
                return cal.getTime();

            default:
                cal.add(Calendar.MONTH, -5);
                return cal.getTime();
        }
    }

    public Map<String, Object> getMonthlyRevenue(String filter) {
        Date fromDate = getFilterDate(filter);

        List<Object[]> rows = ordersRepository.getMonthlyRevenue(fromDate);

        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        List<Integer> growth = new ArrayList<>();

        for (Object[] row : rows) {
            int month = (int) row[0];
            int year = (int) row[1];
            double revenue = ((Number) row[2]).doubleValue();

            labels.add(month + "/" + year);
            values.add(revenue);
        }

        for (int i = 0; i < values.size(); i++) {
            if (i == 0) growth.add(0);
            else {
                double g = ((values.get(i) - values.get(i - 1)) / values.get(i - 1)) * 100;
                growth.add((int) g);
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("labels", labels);
        map.put("values", values);
        map.put("growth", growth);

        return map;
    }

    public Double getTotalRevenue() {
        return ordersRepository.getTotalRevenue();
    }

    public Long getTotalDeliveredOrders() {
        return ordersRepository.getTotalDeliveredOrders();
    }

    public Map<String, Object> getRevenueByCategory() {
        List<Object[]> rows = orderDetailRepository.getRevenueByCategory();

        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for (Object[] r : rows) {
            labels.add((String) r[0]);
            values.add(((Number) r[1]).doubleValue());
        }

        Map<String, Object> map = new HashMap<>();
        map.put("labels", labels);
        map.put("values", values);

        return map;
    }

    public Double getLastMonthRevenue() {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        return ordersRepository.getRevenueOfMonth(month, year);
    }

    public Double getPreviousMonthRevenue() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        return ordersRepository.getRevenueOfMonth(month, year);
    }

    public Double getAverageMonthRevenue() {
        Double total = getTotalRevenue();
        long months = ordersRepository.countRevenueMonths();
        return months == 0 ? 0 : total / months;
    }

    public int getTrendPercent() {
        double last = getLastMonthRevenue();
        double prev = getPreviousMonthRevenue();

        if (prev == 0) return 100; // tăng mạnh
        return (int) (((last - prev) / prev) * 100);
    }

    public List<Books> getRecentSoldBooks() {
        return orderDetailRepository.findRecentSoldBooks(PageRequest.of(0, 10));
    }
}
