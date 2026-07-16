package com.example.project_bookstore.Service;

import com.example.project_bookstore.Untils.VNPayUtils;
import com.example.project_bookstore.dto.PaymentDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_OrderType", orderType);
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", returnUrl);
        params.put("vnp_IpAddr", vnp_IpAddr);

//        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//
//        params.put("vnp_CreateDate", formatter.format(cal.getTime()));
//
//        cal.add(Calendar.MINUTE, 15);
//        params.put("vnp_ExpireDate", formatter.format(cal.getTime()));

        // Khắc phục: Sử dụng múi giờ IANA chuẩn cho Việt Nam (GMT+7)
        TimeZone vietnamTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
        Calendar cal = Calendar.getInstance(vietnamTimeZone);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
// Đảm bảo SimpleDateFormat cũng sử dụng múi giờ chính xác
        formatter.setTimeZone(vietnamTimeZone);

// ... Phần còn lại của code ...
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
        String vnp_SecureHash = vnpParams.get("vnp_SecureHash");

        Map<String, String> fields = new TreeMap<>();
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!"vnp_SecureHash".equals(key) && !"vnp_SecureHashType".equals(key)
                    && value != null && !value.isEmpty()) {
                fields.put(key, value);
            }
        }

        StringBuilder hashData = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                if (hashData.length() > 0) {
                    hashData.append('&');
                }
                // ✅ FIX: phải encode lại value giống hệt lúc tạo payment URL
                hashData.append(entry.getKey())
                        .append('=')
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII.toString()));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        
        String signValue = hmacSHA512(vnp_HashSecret, hashData.toString());

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

    // Thêm vào VNPayService

    @Value("${vnpay.queryUrl}") // https://sandbox.vnpay.vn/merchant_webapi/api/transaction
    private String queryUrl;

    /**
     * Gọi API querydr của VNPay để hỏi lại trạng thái giao dịch thật sự.
     * Dùng khi: đơn đang Pending gần hết hạn 5p nhưng chưa nhận được return/IPN
     * -> tránh hủy nhầm đơn đã bị trừ tiền nhưng mất kết nối lúc redirect về.
     */
    public Map<String, String> queryTransaction(String orderId, Date orderDate) throws Exception {

        String vnp_RequestId = String.valueOf(System.currentTimeMillis());
        String vnp_Version = "2.1.0";
        String vnp_Command = "querydr";
        String vnp_TxnRef = orderId;
        String vnp_OrderInfo = "Truy van GD ma don hang: " + orderId;

        TimeZone vnTz = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        fmt.setTimeZone(vnTz);

        String vnp_TransDate = fmt.format(orderDate);
        Calendar cal = Calendar.getInstance(vnTz);
        String vnp_CreateDate = fmt.format(cal.getTime());
        String vnp_IpAddr = "127.0.0.1";

        String hashData = String.join("|",
                vnp_RequestId, vnp_Version, vnp_Command, tmnCode,
                vnp_TxnRef, vnp_TransDate, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);

        String secureHash = VNPayUtils.hmacSHA512(hashSecret, hashData);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("vnp_RequestId", vnp_RequestId);
        body.put("vnp_Version", vnp_Version);
        body.put("vnp_Command", vnp_Command);
        body.put("vnp_TmnCode", tmnCode);
        body.put("vnp_TxnRef", vnp_TxnRef);
        body.put("vnp_OrderInfo", vnp_OrderInfo);
        body.put("vnp_TransDate", vnp_TransDate);
        body.put("vnp_CreateDate", vnp_CreateDate);
        body.put("vnp_IpAddr", vnp_IpAddr);
        body.put("vnp_SecureHash", secureHash);

        org.springframework.web.client.RestTemplate rt = new org.springframework.web.client.RestTemplate();
        ResponseEntity<Map> resp = rt.postForEntity(queryUrl, body, Map.class);

        Map<String, String> result = new HashMap<>();
        if (resp.getBody() != null) {
            resp.getBody().forEach((k, v) -> result.put(String.valueOf(k), String.valueOf(v)));
        }
        return result;
    }


}

