package com.example.project_bookstore.Export;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class CsvExportService {

    private final JdbcTemplate jdbcTemplate;

    public CsvExportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

//    public void exportBooksToCsv() {
//        String base = System.getProperty("user.dir"); // lấy đường dẫn project đang chạy
//
//        String csvPath = base + "/python/books_latest.csv";
//
//        File path = new File(csvPath);
//        path.getParentFile().mkdirs(); // tạo folder nếu chưa có
//        try (FileWriter writer = new FileWriter(path)) {
//
//            // header
//            writer.write(
//                    "bookId,title,author,publisher,publicationYear," +
//                            "description,price,quantity,picture,categoryId\n"
//            );
//
//            jdbcTemplate.query(
//                    "SELECT * FROM Books",
//                    rs -> {
//                        try {
//                            writer.write(
//                                    rs.getString("bookId") + "," +
//                                            esc(rs.getString("title")) + "," +
//                                            esc(rs.getString("author")) + "," +
//                                            esc(rs.getString("publisher")) + "," +
//                                            rs.getInt("publicationYear") + "," +
//                                            esc(rs.getString("description")) + "," +
//                                            rs.getBigDecimal("price") + "," +
//                                            rs.getInt("quantity") + "," +
//                                            esc(rs.getString("picture")) + "," +
//                                            rs.getString("categoryId") +
//                                            "\n"
//                            );
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//            );
//
//            System.out.println("✅ CSV updated");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    public void exportBooksToCsv() {

        try {
            String baseDir = System.getProperty("user.dir");
            Path folder = Paths.get(baseDir, "python");
            Path csvPath = folder.resolve("books_latest.csv");

            Files.createDirectories(folder);

            System.out.println("=== EXPORT BOOK CSV ===");
            System.out.println("Write to: " + csvPath.toAbsolutePath());

            AtomicInteger rowCount = new AtomicInteger();

            try (BufferedWriter writer = Files.newBufferedWriter(
                    csvPath,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            )) {

                writer.write("bookId,title,author,publisher,publicationYear,"
                        + "description,price,quantity,picture,categoryId\n");

                jdbcTemplate.query(
                        "SELECT * FROM Books",
                        rs -> {
                            rowCount.getAndIncrement();
                            try {
                                writer.write(
                                        rs.getString("bookId") + "," +
                                                esc(rs.getString("title")) + "," +
                                                esc(rs.getString("author")) + "," +
                                                esc(rs.getString("publisher")) + "," +
                                                rs.getInt("publicationYear") + "," +
                                                esc(rs.getString("description")) + "," +
                                                rs.getBigDecimal("price") + "," +
                                                rs.getInt("quantity") + "," +
                                                esc(rs.getString("picture")) + "," +
                                                rs.getString("categoryId") +
                                                "\n"
                                );
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );

                writer.flush();
            }

            System.out.println("✔ CSV exported. Rows: " + rowCount.get());

        } catch (Exception ex) {
            System.err.println("❌ Export CSV failed");
            ex.printStackTrace();
        }
    }


    private String esc(String s) {
        if (s == null) return "";
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
}

