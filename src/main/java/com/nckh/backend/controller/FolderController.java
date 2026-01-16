package com.nckh.backend.controller;

import com.nckh.backend.entity.Folder;
import com.nckh.backend.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FolderController {

    private final FolderService folderService;

    // API: Lấy danh sách thư mục (Bắt buộc phải có userId)
    // GET: http://localhost:8080/api/folders?userId=1
    @GetMapping
    public ResponseEntity<List<Folder>> getAll(@RequestParam("userId") Long userId) {
        System.out.println("--> User " + userId + " lấy danh sách Folder");
        return ResponseEntity.ok(folderService.getAllFolders(userId));
    }

    // API: Tạo thư mục mới (Cần userId để biết của ai)
    // POST: http://localhost:8080/api/folders
    @PostMapping
    public ResponseEntity<?> create(@RequestParam("name") String name,
                                    @RequestParam(value = "desc", required = false, defaultValue = "") String desc,
                                    @RequestParam("userId") Long userId) {
        try {
            System.out.println("--> Tạo folder '" + name + "' cho User ID: " + userId);
            Folder newFolder = folderService.createFolder(name, desc, userId);
            return ResponseEntity.ok(newFolder);
        } catch (Exception e) {
            System.err.println("Lỗi tạo folder: " + e.getMessage());
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // API: Cập nhật tên/mô tả Folder
    // PUT: http://localhost:8080/api/folders/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestParam("name") String name,
                                    @RequestParam(value = "desc", required = false, defaultValue = "") String desc) {
        try {
            return ResponseEntity.ok(folderService.updateFolder(id, name, desc));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // API: Xóa Folder
    // DELETE: http://localhost:8080/api/folders/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            folderService.deleteFolder(id);
            return ResponseEntity.ok("Đã xóa thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}