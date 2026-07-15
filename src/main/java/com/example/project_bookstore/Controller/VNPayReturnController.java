package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Entity.Customers;
import com.example.project_bookstore.Entity.Orders;
import com.example.project_bookstore.Repository.IOrdersRepository;
import com.example.project_bookstore.Service.OrderEmailService;
import com.example.project_bookstore.Service.OrdersService;
import com.example.project_bookstore.Service.VNPayService;
import com.example.project_bookstore.Untils.VNPayUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@RequiredArgsConstructor
@Controller
public class VNPayReturnController {

    private final VNPayService vnPayService;
    private final OrdersService orderService;
    private final OrderEmailService orderEmailService;
    private final IOrdersRepository ordersRepo;

    @GetMapping("/checkout/vnpay_return")
    public String vnpayReturn(HttpServletRequest request) {

        Map<String, String> params = VNPayUtils.getVnPayReturnData(request);

        boolean isValid = vnPayService.validateReturn(params);
        String orderId = params.get("vnp_TxnRef");

        // ===== Chữ ký không hợp lệ -> coi như thất bại, KHÔNG gửi mail =====
        if (!isValid) {
            orderService.markOrderUnPaid(orderId);
            return "redirect:/checkout/fallure?orderId=" + orderId;
        }

        if ("00".equals(params.get("vnp_ResponseCode"))) {
            orderService.markOrderPaid(orderId);

            // ===== CHỈ GỬI MAIL SAU KHI THANH TOÁN THÀNH CÔNG THẬT SỰ =====
            Orders order = ordersRepo.findById(orderId).orElse(null);
            if (order != null) {
                Customers customer = order.getCustomer();
                orderEmailService.sendOrderConfirmationEmail(order, customer);
            }

            return "redirect:/checkout/success?orderId=" + orderId;
        }

        // THẤT BẠI — không gửi mail
        orderService.markOrderUnPaid(orderId);
        return "redirect:/checkout/fallure?orderId=" + orderId;
    }
}