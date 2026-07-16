package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.Orders;
import com.example.project_bookstore.Entity.Payment;
import com.example.project_bookstore.Repository.IPaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private IPaymentRepository paymentRepo;

    public String generateId() {
        List<Payment> list = paymentRepo.findAll();
        if (list.isEmpty()) return "P1";
        List<Integer> ids = new java.util.ArrayList<>();
        for (Payment p : list) {
            ids.add(Integer.parseInt(p.getPaymentId().substring(1)));
        }
        return "P" + (Collections.max(ids) + 1);
    }

    @Transactional
    public void saveVnpayPayment(Orders order) {
        if (paymentRepo.findByOrder_OrderId(order.getOrderId()).isPresent()) return;
        Payment p = new Payment();
        p.setPaymentId(generateId());
        p.setPaymentTime(new Date());
        p.setPaymentMethod("VNPay");
        p.setAmount(order.getTotalAmount());
        p.setOrder(order);
        paymentRepo.save(p);
    }

    @Transactional
    public void saveCodPayment(Orders order) {
        if (paymentRepo.findByOrder_OrderId(order.getOrderId()).isPresent()) return;
        Payment p = new Payment();
        p.setPaymentId(generateId());
        p.setPaymentTime(new Date());
        p.setPaymentMethod("COD");
        p.setAmount(order.getTotalAmount());
        p.setOrder(order);
        paymentRepo.save(p);
    }
}