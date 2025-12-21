package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.OrderDetail;
import com.example.project_bookstore.Entity.Orders;
import com.example.project_bookstore.Repository.IOrderDetailRepository;
import com.example.project_bookstore.Repository.IOrdersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdersService {

    @Autowired
    private IOrdersRepository repo;
    @Autowired
    private IOrderDetailRepository orderDetailRepo;


    public List<Orders> getOrders(String customerId){
        return repo.findByCustomerId(customerId);
    }

    public void updateStatus(String id, String status){
        repo.updateStatus(id, status);
    }

    public Orders getOrderById(String orderId){
        return repo.findById(orderId).orElse(null);
    }
    public void markOrderPaid(String orderId) {
        Orders order = repo.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus("Pending");
            repo.save(order);
        }
    }

    public void markOrderUnPaid(String orderId) {
        Orders order = repo.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus("Cancelled");
            repo.save(order);
        }
    }



    @Transactional
    public void placeOrder(Orders order, List<OrderDetail> details) {

        // 1. Lưu Order
        repo.save(order);

        // 2. Lưu OrderDetail (trigger sẽ chạy ở đây)
        for (OrderDetail detail : details) {
            orderDetailRepo.save(detail);
        }

        // ❌ KHÔNG trừ kho ở đây
        // ❌ KHÔNG check tồn kho ở đây
        // → Trigger MySQL xử lý
    }

}
