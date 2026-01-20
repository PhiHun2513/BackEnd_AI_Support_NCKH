package com.nckh.backend.repository;

import com.nckh.backend.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatSessionIdOrderByTimestampAsc(Long sessionId);
    void deleteByChatSession_Folder_Id(Long folderId);
}