package com.nckh.backend.controller;

import com.nckh.backend.entity.ChatMessage;
import com.nckh.backend.service.ChatService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;

    @Data
    static class MessageRequest {
        private Long folderId;
        private String role;
        private String content;
    }

    // API: Lưu tin nhắn (Python gọi sau khi user hỏi hoặc AI trả lời)
    @PostMapping("/save")
    public ResponseEntity<?> saveMessage(@RequestBody MessageRequest request) {
        chatService.saveMessage(request.getFolderId(), request.getRole(), request.getContent());
        return ResponseEntity.ok("Đã lưu tin nhắn");
    }

    // API: Lấy lịch sử chat của Folder (Python gọi khi chọn Folder)
    @GetMapping("/history/{folderId}")
    public ResponseEntity<List<ChatMessage>> getHistory(@PathVariable Long folderId) {
        return ResponseEntity.ok(chatService.getHistory(folderId));
    }
}