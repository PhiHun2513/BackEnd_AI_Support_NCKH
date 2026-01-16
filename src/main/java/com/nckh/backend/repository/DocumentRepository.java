package com.nckh.backend.repository;

import com.nckh.backend.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    boolean existsByFolderIdAndFileName(Long folderId, String fileName);
    List<Document> findByFolderId(Long folderId);


}