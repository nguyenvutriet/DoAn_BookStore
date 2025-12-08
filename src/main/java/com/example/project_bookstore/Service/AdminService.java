package com.example.project_bookstore.Service;

import com.example.project_bookstore.Repository.IOrdersRepository;
import com.example.project_bookstore.Repository.IReviewRepository;
import com.example.project_bookstore.Repository.IUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private IUsersRepository usersRepository;

    @Autowired
    private IUsersRepository booksRepository;

    @Autowired
    private IUsersRepository customersRepository;

    @Autowired
    private IOrdersRepository ordersRepository;

    @Autowired
    private IReviewRepository reviewRepository;

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
}
