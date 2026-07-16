package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Entity.Orders;
import com.example.project_bookstore.Repository.IOrdersRepository;
import com.example.project_bookstore.Service.OrdersService;
import com.example.project_bookstore.Service.VNPayService;
import com.example.project_bookstore.Untils.VNPayUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class VNPayIPNController {

    private final VNPayService vnPayService;
    private final OrdersService orderService;
    private final IOrdersRepository ordersRepo;

    // URL này khai báo trong merchant portal VNPay (mục IPN URL), KHÁC với vnp_ReturnUrl
    @GetMapping("/checkout/vnpay_ipn")
    @ResponseBody
    public ResponseEntity<Map<String, String>> ipn(HttpServletRequest request) {

        Map<String, String> params = VNPayUtils.getVnPayReturnData(request);
        Map<String, String> response = new HashMap<>();

        boolean isValid = vnPayService.validateReturn(params);
        if (!isValid) {
            response.put("RspCode", "97");
            response.put("Message", "Invalid Signature");
            return ResponseEntity.ok(response);
        }

        String orderId = params.get("vnp_TxnRef");
        Orders order = ordersRepo.findById(orderId).orElse(null);

        if (order == null) {
            response.put("RspCode", "01");
            response.put("Message", "Order not found");
            return ResponseEntity.ok(response);
        }

        // ===== IDEMPOTENCY: nếu đã xử lý rồi (Confirmed hoặc Cancelled) thì báo Confirm Success
        // luôn, không xử lý lại -> tránh trùng đơn khi VNPay gọi lại IPN nhiều lần =====
        if ("Confirmed".equals(order.getStatus())) {
            response.put("RspCode", "02");
            response.put("Message", "Order already confirmed");
            return ResponseEntity.ok(response);
        }

        String amountStr = params.get("vnp_Amount");
        long vnpAmount = Long.parseLong(amountStr) / 100;
        if (order.getTotalAmount().longValue() != vnpAmount) {
            response.put("RspCode", "04");
            response.put("Message", "Invalid amount");
            return ResponseEntity.ok(response);
        }

        if ("00".equals(params.get("vnp_ResponseCode"))) {
            orderService.markOrderPaid(orderId);
        } else {
            orderService.markOrderUnPaid(orderId);
        }

        response.put("RspCode", "00");
        response.put("Message", "Confirm Success");
        return ResponseEntity.ok(response);
    }
}