package com.nckh.backend.service;

import com.nckh.backend.entity.Document;
import com.nckh.backend.entity.DocumentContent;
import com.nckh.backend.entity.Folder;
import com.nckh.backend.repository.DocumentRepository;
import com.nckh.backend.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final FolderRepository folderRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public Document uploadFile(MultipartFile file, Long folderId, String extractedText) {
        //  Kiểm tra trùng
        if (documentRepository.existsByFolderIdAndFileName(folderId, file.getOriginalFilename())) {
            throw new RuntimeException("File đã tồn tại!");
        }

        // Tìm Folder
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Folder ID: " + folderId));

        //Gọi Thủ kho lưu file ra ổ cứng -> Lấy đường dẫn
        String savedFilePath = fileStorageService.storeFileToDisk(file);

        // Lưu thông tin vào Database
        Document doc = new Document();
        doc.setFileName(file.getOriginalFilename());
        doc.setFileType(file.getContentType());
        doc.setFileSize(file.getSize());
        doc.setUploadTime(LocalDateTime.now());
        doc.setFilePath(savedFilePath);
        doc.setFolder(folder);

        // Lưu nội dung text
        DocumentContent content = new DocumentContent();
        content.setExtractedText(extractedText);
        content.setDocument(doc);
        doc.setDocumentContent(content);

        return documentRepository.save(doc);
    }

    @Transactional
    public void deleteDocument(Long documentId) {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("File không tồn tại!"));
        fileStorageService.deleteFileFromDisk(doc.getFilePath());
        documentRepository.delete(doc);
    }

    public List<Document> getDocumentsByFolder(Long folderId) {
        return documentRepository.findByFolderId(folderId);
    }

    @Transactional(readOnly = true)
    public String getFolderContext(Long folderId) {
        List<Document> docs = documentRepository.findByFolderId(folderId);
        return docs.stream()
                .map(doc -> doc.getDocumentContent() != null ? doc.getDocumentContent().getExtractedText() : "")
                .collect(Collectors.joining("\n\n"));
    }
    // DOWNLOAD
    public Document getDocumentById(Long docId) {
        return documentRepository.findById(docId)
                .orElseThrow(() -> new RuntimeException("File không tồn tại"));
    }

    public Resource loadDocumentAsResource(Long docId) {
        Document doc = getDocumentById(docId);
        return fileStorageService.loadFileAsResource(doc.getFilePath());
    }

    // LẤY TẤT CẢ FILE CHO ADMIN
    public List<Document> getAllDocumentsAdmin() {
        return documentRepository.findAll();
    }
}