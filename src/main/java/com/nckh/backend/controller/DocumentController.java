package com.nckh.backend.controller;

import com.nckh.backend.dto.DocumentResponse;
import com.nckh.backend.entity.Document;
import com.nckh.backend.repository.DocumentRepository;
import com.nckh.backend.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DocumentController {

    private final FileStorageService fileStorageService;
    private final DocumentRepository documentRepository;

    @PostMapping("/upload")
    public ResponseEntity<DocumentResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folderId", required = false) Long folderId) {

        return ResponseEntity.ok(fileStorageService.storeFile(file, folderId));
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getAllFiles() {
        return ResponseEntity.ok(fileStorageService.getAllFiles());
    }
    // API: Lấy danh sách file thuộc về một Folder cụ thể
    // GET: http://localhost:8080/api/documents/folder/1
    @GetMapping("/folder/{folderId}")
    public ResponseEntity<List<DocumentResponse>> getFilesByFolder(@PathVariable Long folderId) {
        // 1. Gọi Repository tìm file theo ID thư mục
        List<Document> docs = documentRepository.findByFolderId(folderId);

        // 2. Chuyển đổi sang dạng DTO để trả về
        List<DocumentResponse> responses = docs.stream()
                .map(doc -> DocumentResponse.builder()
                        .id(doc.getId())
                        .fileName(doc.getFileName())
                        .fileType(doc.getFileType())
                        .fileSize(doc.getFileSize())
                        .uploadTime(doc.getUploadedAt())
                        // Nếu cần downloadUrl thì có thể map thêm ở đây
                        .build())
                .toList();

        return ResponseEntity.ok(responses);
    }
}