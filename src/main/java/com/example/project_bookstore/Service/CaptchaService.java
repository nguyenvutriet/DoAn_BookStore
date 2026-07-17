package com.example.project_bookstore.Service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class CaptchaService {

    private static final String SECRET_KEY =
            "6Leol1gtAAAAALDpebFsPfC4ZxHao9ZY-62oExqf";

    public boolean verify(String token) {

        try {

            String params =
                    "secret=" + SECRET_KEY +
                            "&response=" + token;

            byte[] postData = params.getBytes("UTF-8");

            URL url =
                    new URL("https://www.google.com/recaptcha/api/siteverify");

            HttpURLConnection conn =
                    (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            conn.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
            );

            conn.setRequestProperty(
                    "Content-Length",
                    String.valueOf(postData.length)
            );

            conn.getOutputStream().write(postData);

            BufferedReader br =
                    new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream()
                            )
                    );

            StringBuilder sb = new StringBuilder();

            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            System.out.println(sb.toString());

            JSONObject json =
                    new JSONObject(sb.toString());

            return json.getBoolean("success");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}