package com.example.project_bookstore.Export;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CsvAutoScheduler {

    private final CsvExportService csvExportService;

    public CsvAutoScheduler(CsvExportService csvExportService) {
        this.csvExportService = csvExportService;
    }

    // chạy mỗi 5 phút
    @Scheduled(fixedRate = 120_000)
    public void autoExportBooksCsv() {
        csvExportService.exportBooksToCsv();
    }
}
