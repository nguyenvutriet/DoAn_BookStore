package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.Orders;
import com.example.project_bookstore.Repository.IOrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdersService {

    @Autowired
    private IOrdersRepository repo;


    public List<Orders> getOrders(String customerId){
        return repo.findByCustomerId(customerId);
    }

}
