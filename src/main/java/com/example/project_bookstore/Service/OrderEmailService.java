package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.Customers;
import com.example.project_bookstore.Entity.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;

@Service
public class OrderEmailService {

    @Autowired
    private EmailService emailService;

    /**
     * Gửi email xác nhận đơn hàng.
     * - COD: gọi ngay sau khi đặt hàng thành công (CheckoutController.submitOrder).
     * - VNPay: gọi từ VNPayReturnController, chỉ sau khi thanh toán thành công thật sự.
     */
    public void sendOrderConfirmationEmail(Orders order, Customers customer) {
        try {
            Context context = new Context();

            BigDecimal totalProductAmount = order.getOrderDetail_Order()
                    .stream()
                    .map(d -> d.getUnitPrice().multiply(BigDecimal.valueOf(d.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal shippingFee = order.getTotalAmount().subtract(totalProductAmount);

            context.setVariable("order", order);
            context.setVariable("details", order.getOrderDetail_Order());
            context.setVariable("customer", customer);
            context.setVariable("totalProductAmount", totalProductAmount);
            context.setVariable("shippingFee", shippingFee);

            emailService.sendHtmlEmail(
                    customer.getEmail(),
                    "Xác nhận đơn hàng #" + order.getOrderId(),
                    "order-email",
                    context
            );
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[EMAIL] Gửi email thất bại!");
        }
    }
}