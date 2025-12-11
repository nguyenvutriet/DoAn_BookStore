package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Entity.*;
import com.example.project_bookstore.Repository.*;
import com.example.project_bookstore.Service.VNPayService;
import com.example.project_bookstore.dto.PaymentDTO;
import com.example.project_bookstore.dto.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/payments")
public class PaymentController {


    private final VNPayService vnPayService;

    @PostMapping("/create_payment_url")
    public ResponseEntity<ResponseObject> createPayment(
            @RequestBody PaymentDTO paymentRequest,
            HttpServletRequest request) {
        try {
            String paymentUrl = vnPayService.createPaymentUrl(paymentRequest, request);

            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Payment URL generated successfully.")
                            .data(paymentUrl)
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            ResponseObject.builder()
                                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .message("Error generating payment URL: " + e.getMessage())
                                    .build()
                    );
        }
    }

}


