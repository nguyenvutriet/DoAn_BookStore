package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.Orders;
import com.example.project_bookstore.Repository.IOrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

@Service
public class PredictService {

    @Autowired
    private IOrdersRepository repo;

    public File exportToCSV() {

        List<Object[]> data = repo.findDailyRevenue();
        File file = new File("src/main/resources/static/python/orders.csv");

        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            pw.println("orderDate,totalAmount");

            for (Object[] order : data) {
                pw.println(order[0] + "," + order[1]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("CSV size: " + file.length());
        return file;
    }


    public String runPredictModel(){
        String lastLine = "";

        try {
            String scriptPath = Paths
                    .get("src/main/resources/static/python/predict.py")
                    .toAbsolutePath()
                    .toString();

            ProcessBuilder pb = new ProcessBuilder(
                    "C:/Users/MSI GF63/AppData/Local/Programs/Python/Python313/python.exe",
                    "D:/HCMUTE_IT/HK1_2025-2026/Lap_Trinh_Web/ThucHanh/Project_BookStore/src/main/resources/static/python/predict.py"
            );

            // ❌ KHÔNG gộp stderr nữa
            // pb.redirectErrorStream(true);

            // Ép Python UTF-8 cho stdout
            pb.environment().put("PYTHONIOENCODING", "utf-8");

            Process process = pb.start();

            // ✅ CHỈ ĐỌC STDOUT (JSON)
            BufferedReader stdout = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
            );

            String line;
            while ((line = stdout.readLine()) != null) {
                lastLine = line; // JSON ở dòng cuối
            }

            // 🔍 Đọc stderr để debug (KHÔNG trả về client)
            BufferedReader stderr = new BufferedReader(
                    new InputStreamReader(process.getErrorStream())
            );
            String err;
            while ((err = stderr.readLine()) != null) {
                System.err.println("[PYTHON ERROR] " + err);
            }

            return lastLine.isEmpty()
                    ? "{\"error\":\"Python returned empty output\"}"
                    : lastLine;

        } catch (Exception e) {
            return "{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}";
        }
    }

}
