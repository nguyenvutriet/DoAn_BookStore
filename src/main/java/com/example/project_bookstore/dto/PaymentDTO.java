package com.example.project_bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private long amount;      // Số tiền thanh toán
    private String orderId;     // ID đơn hàng
    private String orderInfo; // Nội dung giao dịch (tuỳ chọn)
}

