package com.example.project_bookstore.Untils;

import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;

public class VNPayUtils {

    public static String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            hmac512.init(secretKey);

            byte[] bytes = hmac512.doFinal(data.getBytes("UTF-8"));
            StringBuilder hash = new StringBuilder();

            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }

            return hash.toString();
        } catch (Exception e) {
            return "";
        }
    }
    //Lấy toàn bộ params VNPay trả về từ request
    public static Map<String, String> getVnPayReturnData(HttpServletRequest request) {

        Map<String, String> fields = new HashMap<>();

        Map<String, String[]> requestParams = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue()[0]; // lấy phần tử đầu tiên
            fields.put(key, value);
        }

        return fields;
    }
}

