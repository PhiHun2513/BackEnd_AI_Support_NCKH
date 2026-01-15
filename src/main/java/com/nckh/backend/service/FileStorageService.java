package com.nckh.backend.service;

import com.nckh.backend.dto.DocumentResponse;
import com.nckh.backend.entity.Document;
import com.nckh.backend.entity.Folder;
import com.nckh.backend.repository.DocumentRepository;
import com.nckh.backend.repository.FolderRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final DocumentRepository documentRepository;
    private final FolderRepository folderRepository; // Đã thêm Repository này
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tạo thư mục lưu trữ file.", ex);
        }
    }

    /**
     * Lưu file vào ổ cứng và lưu thông tin vào Database.
     * @param file File từ người dùng gửi lên.
     * @param folderId ID của thư mục (có thể null nếu không chọn thư mục).
     */
    public DocumentResponse storeFile(MultipartFile file, Long folderId) {
        // 1. Làm sạch tên file
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) originalFileName = "unknown_file";

        // Tạo tên file duy nhất để tránh trùng lặp (dùng thời gian hệ thống)
        String fileName = System.currentTimeMillis() + "_" + originalFileName;

        try {
            // 2. Lưu file vật lý vào ổ cứng
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 3. Chuẩn bị đối tượng Document (Builder Pattern)
            var docBuilder = Document.builder()
                    .fileName(originalFileName)
                    .fileType(file.getContentType())
                    .filePath(targetLocation.toString())
                    .fileSize(file.getSize());

            // 4. Xử lý Logic gán Folder (Nếu người dùng có chọn)
            if (folderId != null) {
                Folder folder = folderRepository.findById(folderId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy Folder với ID: " + folderId));
                docBuilder.folder(folder);
            }

            // 5. Lưu vào Database
            Document savedDoc = documentRepository.save(docBuilder.build());

            // 6. Trả về kết quả DTO
            return mapToDTO(savedDoc);

        } catch (IOException ex) {
            throw new RuntimeException("Không thể lưu file " + fileName + ". Vui lòng thử lại!", ex);
        }
    }

    public List<DocumentResponse> getAllFiles() {
        return documentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Hàm chuyển đổi từ Entity sang DTO để trả về cho Frontend
    private DocumentResponse mapToDTO(Document doc) {
        // Tạo link download (nếu sau này cần tính năng tải xuống)
        String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/documents/download/")
                .path(doc.getId().toString())
                .toUriString();

        return DocumentResponse.builder()
                .id(doc.getId())
                .fileName(doc.getFileName())
                .fileType(doc.getFileType())
                .fileSize(doc.getFileSize())
                .uploadTime(doc.getUploadedAt()) // Đảm bảo Entity Document đã có getter này
                .downloadUrl(downloadUrl)
                .build();
    }
}