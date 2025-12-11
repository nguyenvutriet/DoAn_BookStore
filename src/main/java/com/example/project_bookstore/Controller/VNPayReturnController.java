package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Entity.*;
import com.example.project_bookstore.Repository.*;
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



    @GetMapping("/checkout/vnpay_return")
    public String vnpayReturn(HttpServletRequest request) {

        Map<String, String> params = VNPayUtils.getVnPayReturnData(request);

        boolean isValid = vnPayService.validateReturn(params);

        String orderId = params.get("vnp_TxnRef");   // 🟢 orderId dạng String

        if ("00".equals(params.get("vnp_ResponseCode"))) {
            orderService.markOrderPaid(orderId);
            return "redirect:/checkout/success?orderId=" + orderId;
        }


        // THẤT BẠI
        orderService.markOrderUnPaid(orderId);
        return "redirect:/checkout/fallure?orderId=" + orderId;
    }

}
