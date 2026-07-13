package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.OrderDetail;
import com.example.project_bookstore.Entity.Orders;
import com.example.project_bookstore.Repository.IOrderDetailRepository;
import com.example.project_bookstore.Repository.IOrdersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class OrdersService {

    @Autowired
    private IOrdersRepository repo;
    @Autowired
    private IOrderDetailRepository orderDetailRepo;

    @Autowired
    private com.example.project_bookstore.Service.FlashSaleService flashSaleService;

    public String generateId(){
        List<Orders> orders = repo.findAll();
        List<Integer> dsSo = new java.util.ArrayList();
        for(Orders o : orders){
            int id = Integer.parseInt(o.getOrderId().substring(1));
            dsSo.add(id);
        }
        int idMax = Collections.max(dsSo);
        idMax = idMax+1;
        return "O" + idMax;
    }


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

        // 2. Trừ kho flash sale nếu cần, rồi lưu OrderDetail
        for (OrderDetail detail : details) {
            String bookId = detail.getBook().getBookId();
            java.util.Optional<com.example.project_bookstore.Entity.FlashSaleDetail> optFs = flashSaleService.getActiveSaleForBook(bookId);
            if (optFs.isPresent()) {
                String flashSaleId = optFs.get().getFlashSale().getFlashSaleId();
                // reserveStock sẽ throw nếu không đủ — khiến toàn bộ transaction rollback
                flashSaleService.reserveStock(flashSaleId, bookId, detail.getQuantity());
            }
            orderDetailRepo.save(detail);
        }

        // Ghi chú: nếu reserveStock ném ngoại lệ, transaction sẽ rollback toàn bộ đặt hàng
    }

    public List<Orders> getOrdersByStatus(String customerId, String status){
        return repo.findByCustomerIdAndStatus(customerId, status);
    }
}
