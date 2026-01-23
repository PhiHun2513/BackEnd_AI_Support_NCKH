package com.nckh.backend.controller;

import com.nckh.backend.entity.Document;
import com.nckh.backend.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentService documentService;

    // POST: http://localhost:8080/api/documents/upload
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @RequestParam("folderId") Long folderId,
                                    @RequestParam("extractedText") String extractedText) {
        try {
            Document doc = documentService.uploadFile(file, folderId, extractedText);
            return ResponseEntity.ok(doc);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET: http://localhost:8080/api/documents/folder/{folderId}
    @GetMapping("/folder/{folderId}")
    public ResponseEntity<List<Document>> getDocuments(@PathVariable Long folderId) {
        return ResponseEntity.ok(documentService.getDocumentsByFolder(folderId));
    }

    // GET: http://localhost:8080/api/documents/folder/{folderId}/context
    @GetMapping("/folder/{folderId}/context")
    public ResponseEntity<String> getFolderContext(@PathVariable Long folderId) {
        return ResponseEntity.ok(documentService.getFolderContext(folderId));
    }


    // DELETE: http://localhost:8080/api/documents/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            documentService.deleteDocument(id);
            return ResponseEntity.ok("Xóa thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // GET: http://localhost:8080/api/documents/admin/all
    @GetMapping("/admin/all")
    public ResponseEntity<List<Document>> getAllDocumentsForAdmin() {
        return ResponseEntity.ok(documentService.getAllDocumentsAdmin());
    }
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        Document doc = documentService.getDocumentById(id);
        Resource resource = documentService.loadDocumentAsResource(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                .body(resource);
    }
}