package com.nckh.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.net.MalformedURLException;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public FileStorageService() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tạo thư mục lưu trữ file.", ex);
        }
    }

    public String storeFileToDisk(MultipartFile file) {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return targetLocation.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Lỗi lưu file vào ổ cứng: " + fileName, ex);
        }
    }

    public void deleteFileFromDisk(String filePath) {
        if (filePath == null || filePath.isEmpty()) return;
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException ex) {
            System.err.println("Không xóa được file vật lý: " + filePath);
        }
    }
    public Resource loadFileAsResource(String filePath) {
        try {
            Path filePathObj = Paths.get(filePath);
            Resource resource = new UrlResource(filePathObj.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File không tìm thấy: " + filePath);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File không tìm thấy: " + filePath, ex);
        }
    }
}