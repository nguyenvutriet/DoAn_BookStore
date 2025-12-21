package com.example.project_bookstore.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;

@Service
public class GoogleDriveService {

    private static final String APPLICATION_NAME = "UTE-BookStore";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    // =======================
    // 1. KẾT NỐI GOOGLE DRIVE
    // =======================
    public Drive getDriveService() throws Exception {

        InputStream in = getClass()
                .getResourceAsStream("/drive/service-account.json");

        if (in == null) {
            throw new RuntimeException("Không tìm thấy service-account.json");
        }

        GoogleCredential credential = GoogleCredential.fromStream(in)
                .createScoped(Collections.singleton(DriveScopes.DRIVE_READONLY));

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential
        ).setApplicationName(APPLICATION_NAME)
                .build();
    }

    // =====================================
    // 2. TÌM FILE ID THEO BOOK CODE (TÊN FILE)
    // =====================================
    public String findFileIdByBookCode(String bookCode) throws Exception {

        Drive drive = getDriveService();

        String fileName = bookCode + ".pdf";
        String query = "name = '" + fileName + "' and trashed = false";

        FileList result = drive.files()
                .list()
                .setQ(query)
                .setFields("files(id, name)")
                .execute();

        if (result.getFiles() == null || result.getFiles().isEmpty()) {
            throw new RuntimeException("Không tìm thấy file đọc thử: " + fileName);
        }

        // Lấy file đầu tiên (giả định bookCode là duy nhất)
        return result.getFiles().get(0).getId();
    }

    // ==========================
    // 3. DOWNLOAD FILE THEO FILE ID
    // ==========================
    public byte[] downloadFileById(String fileId) throws Exception {

        Drive drive = getDriveService();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        drive.files()
                .get(fileId)
                .setAlt("media")
                .executeMediaAndDownloadTo(outputStream);

        return outputStream.toByteArray();
    }

}
