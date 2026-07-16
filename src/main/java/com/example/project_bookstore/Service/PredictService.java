package com.example.project_bookstore.Service;

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


    public String runPredictModel() {

        try {

            String scriptPath = Paths.get(
                    "src/main/resources/static/python/predict.py"
            ).toAbsolutePath().toString();

            System.out.println("================================");
            System.out.println("Script: " + scriptPath);
            System.out.println("Exists: " + new File(scriptPath).exists());
            System.out.println("================================");

            ProcessBuilder pb = new ProcessBuilder(
                    "python",
                    scriptPath
            );

            pb.environment().put("PYTHONIOENCODING", "utf-8");

            Process process = pb.start();

            BufferedReader stdout = new BufferedReader(
                    new InputStreamReader(
                            process.getInputStream(),
                            StandardCharsets.UTF_8
                    )
            );

            StringBuilder output = new StringBuilder();
            String line;

            while ((line = stdout.readLine()) != null) {
                output.append(line);
            }

            BufferedReader stderr = new BufferedReader(
                    new InputStreamReader(
                            process.getErrorStream(),
                            StandardCharsets.UTF_8
                    )
            );

            StringBuilder error = new StringBuilder();
            String err;

            while ((err = stderr.readLine()) != null) {
                error.append(err).append("\n");
            }

            int exitCode = process.waitFor();

            System.out.println("Exit Code: " + exitCode);

            if (!error.isEmpty()) {
                System.out.println("Python Error:");
                System.out.println(error);
            }

            if (output.isEmpty()) {
                return "{\"error\":\"Python returned empty output\"}";
            }

            return output.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"" +
                    e.getMessage().replace("\"", "'")
                    + "\"}";
        }
    }

}
