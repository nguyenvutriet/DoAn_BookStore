package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.*;
import com.example.project_bookstore.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void deleteCustomer(String id) {
        customersRepository.deleteById(id);
    }

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

    public void deleteOrder(String id) {
        ordersRepository.deleteById(id);
    }

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
}
