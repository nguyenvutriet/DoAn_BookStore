package com.example.project_bookstore.Service;

import com.example.project_bookstore.Repository.IOrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PythonService {
    @Autowired
    private IOrdersRepository repo;

    public File exportToCSV() {

        List<Object[]> data = repo.findDailyRevenue();
        //File file = new File("src/main/resources/static/python/orders.csv");
        String base = System.getProperty("user.dir"); // lấy đường dẫn project đang chạy

        String csvPath = base + "/python/orders.csv";

        File file = new File(csvPath);
        file.getParentFile().mkdirs(); // tạo folder nếu chưa có

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


    private String runScript(String scriptName, String... args) {

        try {
            String scriptPath = Paths.get(
                    "python",
                    scriptName
            ).toAbsolutePath().toString();

            // build command
            ProcessBuilder pb = new ProcessBuilder();
            pb.command().add("python3");   // cấu hình PATH trước
            pb.command().add(scriptPath);

            for (String arg : args) {
                pb.command().add(arg);
            }

            pb.environment().put("PYTHONIOENCODING", "utf-8");

            Process p = pb.start();

            BufferedReader out = new BufferedReader(
                    new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8)
            );
            String json = out.lines().collect(Collectors.joining());

            BufferedReader err = new BufferedReader(
                    new InputStreamReader(p.getErrorStream(), StandardCharsets.UTF_8)
            );
            err.lines().forEach(System.err::println);

            p.waitFor();

            return json.isBlank() ? "[]" : json;

        } catch (Exception e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    // gọi autocomplete python
    public String findBookByTitle(String title) {
        return runScript("find_book.py", title);
    }

    // gọi ml.py
    public String runPythonModel(String bookId) {
        return runScript("ml.py", bookId);
    }

    //gọi predict.py
    public String runPredictModel() {
        return runScript("predict.py");}
}