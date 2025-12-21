package com.example.project_bookstore.Untils;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class PdfUtils {

    /**
     * Cắt 5 trang đầu của file PDF
     */
    public static byte[] cutFirstFivePages(byte[] pdfBytes) throws Exception {

        PDDocument source = PDDocument.load(new ByteArrayInputStream(pdfBytes));
        PDDocument preview = new PDDocument();

        int totalPages = source.getNumberOfPages();
        int maxPages = Math.min(5, totalPages);

        for (int i = 0; i < maxPages; i++) {
            preview.addPage(source.getPage(i));
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        preview.save(outputStream);

        source.close();
        preview.close();

        return outputStream.toByteArray();
    }
}
