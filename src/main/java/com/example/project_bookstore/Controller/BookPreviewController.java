package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Entity.Books;
import com.example.project_bookstore.Service.BooksService;
import com.example.project_bookstore.Service.GoogleDriveService;
import com.example.project_bookstore.Untils.PdfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookPreviewController {

    @Autowired
    private BooksService booksService;

    @Autowired
    private GoogleDriveService googleDriveService;

    @GetMapping("/api/books/{bookId}/preview")
    public ResponseEntity<byte[]> previewBook(@PathVariable String bookId) {

        try {
            System.out.println("=== PREVIEW BOOK === " + bookId);

            Books book = booksService.getBookById(bookId);
            String bookCode = book.getBookId();

            System.out.println("BookCode = " + bookCode);

            String fileId = googleDriveService.findFileIdByBookCode(bookCode);
            System.out.println("FileId = " + fileId);

            byte[] fullPdf = googleDriveService.downloadFileById(fileId);
            System.out.println("PDF size = " + fullPdf.length);

            byte[] previewPdf = PdfUtils.cutFirstFivePages(fullPdf);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=preview.pdf")
                    .body(previewPdf);

        } catch (Exception e) {
            e.printStackTrace(); // 🔥 BẮT BUỘC
            return ResponseEntity.internalServerError().build();
        }
    }

}
