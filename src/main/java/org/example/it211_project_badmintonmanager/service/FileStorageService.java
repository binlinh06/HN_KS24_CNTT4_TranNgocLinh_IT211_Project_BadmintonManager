package org.example.it211_project_badmintonmanager.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    // Thư mục chứa ảnh sẽ tự động được tạo ra ở thư mục gốc của project
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public FileStorageService() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tạo thư mục lưu trữ file.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        try {
            // Tạo tên file ngẫu nhiên để không bị trùng (vd: 123e4567-e89b..._anhsan1.jpg)
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // Copy file vào thư mục uploads
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);

            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Không thể lưu file " + file.getOriginalFilename() + ". Vui lòng thử lại!", ex);
        }
    }
}