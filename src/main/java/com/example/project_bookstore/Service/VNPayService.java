package com.example.project_bookstore.Service;

import com.example.project_bookstore.Untils.VNPayUtils;
import com.example.project_bookstore.dto.PaymentDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.project_bookstore.Untils.VNPayUtils.hmacSHA512;

@Service
@RequiredArgsConstructor
public class VNPayService {

    @Value("${vnpay.tmnCode}")
    private String tmnCode;

    @Value("${vnpay.hashSecret}")
    private String hashSecret;

    @Value("${vnpay.payUrl}")
    private String payUrl;

    @Value("${vnpay.returnUrl}")
    private String returnUrl;

    public String createPaymentUrl(PaymentDTO dto, HttpServletRequest request) throws UnsupportedEncodingException {

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "order";

        long amount = dto.getAmount() * 100;

        String vnp_TxnRef = dto.getOrderId();
        String vnp_IpAddr = request.getRemoteAddr();

        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", vnp_Version);
        params.put("vnp_Command", vnp_Command);
        params.put("vnp_TmnCode", tmnCode);
        params.put("vnp_Amount", String.valueOf(amount));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", vnp_TxnRef);
        // ✔ FIX NULL
        String orderInfo = dto.getOrderInfo();
        if (orderInfo == null || orderInfo.isEmpty()) {
            orderInfo = "Thanh toan don hang #" + dto.getOrderId();
        }
        params.put("vnp_OrderInfo", dto.getOrderInfo());
        if (orderInfo == null) orderInfo = "Thanh toan don hang";
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_OrderType", orderType);
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", returnUrl);
        params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        params.put("vnp_CreateDate", formatter.format(cal.getTime()));

        cal.add(Calendar.MINUTE, 15);
        params.put("vnp_ExpireDate", formatter.format(cal.getTime()));

        // Build hashdata & query
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            hashData.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII.toString()));
            query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII.toString()))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII.toString()));

            query.append("&");
            hashData.append("&");
        }

        hashData.setLength(hashData.length() - 1);
        query.setLength(query.length() - 1);

        String secureHash = VNPayUtils.hmacSHA512(hashSecret, hashData.toString());
        String paymentUrl = payUrl + "?" + query + "&vnp_SecureHash=" + secureHash;

        return paymentUrl;
    }

    @Value("${vnpay.hashSecret}")
    private String vnp_HashSecret;

    // ... các hàm khác (createPaymentUrl, v.v.)

    public boolean validateReturn(Map<String, String> vnpParams) {
        // Lấy secure hash VNPay gửi về
        String vnp_SecureHash = vnpParams.get("vnp_SecureHash");

        // Bỏ 2 field hash ra khỏi data dùng để ký
        Map<String, String> fields = new TreeMap<>();
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!"vnp_SecureHash".equals(key) && !"vnp_SecureHashType".equals(key)) {
                fields.put(key, value);
            }
        }

        // Ghép data để ký lại
        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (hashData.length() > 0) {
                hashData.append('&');
            }
            hashData.append(entry.getKey()).append('=').append(entry.getValue());
        }

        // Ký lại bằng HMAC-SHA512 với hashSecret
        String signValue = hmacSHA512(vnp_HashSecret, hashData.toString());

        // So sánh với chữ ký VNPay gửi về
        return signValue != null && signValue.equalsIgnoreCase(vnp_SecureHash);
    }

    private String hmacSHA512(String key, String data) {
        try {
            javax.crypto.Mac hmac512 = javax.crypto.Mac.getInstance("HmacSHA512");
            javax.crypto.spec.SecretKeySpec secretKey =
                    new javax.crypto.spec.SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes("UTF-8"));

            StringBuilder hash = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hash.append('0');
                hash.append(hex);
            }
            return hash.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}

