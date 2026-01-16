package com.nckh.backend.repository;

import com.nckh.backend.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    boolean existsByFolderName(String folderName);
    List<Folder> findByUserId(Long userId);
}