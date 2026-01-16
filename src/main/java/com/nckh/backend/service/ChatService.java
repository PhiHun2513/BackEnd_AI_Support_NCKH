package com.nckh.backend.service;

import com.nckh.backend.entity.ChatMessage;
import com.nckh.backend.entity.ChatSession;
import com.nckh.backend.entity.Folder;
import com.nckh.backend.repository.ChatMessageRepository;
import com.nckh.backend.repository.ChatSessionRepository;
import com.nckh.backend.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final FolderRepository folderRepository;


    // Mỗi folder chỉ cần 1 phiên chat chính để lưu lịch sử)
    public ChatSession getOrCreateSession(Long folderId) {
        List<ChatSession> sessions = sessionRepository.findByFolderId(folderId);
        if (!sessions.isEmpty()) {
            return sessions.get(0);
        }
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder không tồn tại"));

        ChatSession newSession = ChatSession.builder()
                .sessionTitle("Hội thoại chính")
                .folder(folder)
                .build();
        return sessionRepository.save(newSession);
    }

    //  Lưu tin nhắn mới
    public void saveMessage(Long folderId, String role, String content) {
        ChatSession session = getOrCreateSession(folderId);

        ChatMessage message = ChatMessage.builder()
                .role(role)
                .content(content)
                .chatSession(session)
                .build();
        messageRepository.save(message);
    }

    //  Lấy lịch sử tin nhắn
    public List<ChatMessage> getHistory(Long folderId) {
        ChatSession session = getOrCreateSession(folderId);
        return messageRepository.findByChatSessionIdOrderByTimestampAsc(session.getId());
    }
}