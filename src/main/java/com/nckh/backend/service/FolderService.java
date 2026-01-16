package com.nckh.backend.service;

import com.nckh.backend.entity.Document;
import com.nckh.backend.entity.Folder;
import com.nckh.backend.entity.User;
import com.nckh.backend.repository.DocumentRepository;
import com.nckh.backend.repository.FolderRepository;
import com.nckh.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final FileStorageService fileStorageService;


    public List<Folder> getAllFolders(Long userId) {
        return folderRepository.findByUserId(userId);
    }

    public Folder createFolder(String name, String desc, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Folder folder = Folder.builder()
                .folderName(name)
                .description(desc)
                .user(user)
                .build();

        return folderRepository.save(folder);
    }

    public Folder updateFolder(Long id, String name, String desc) {
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Folder không tồn tại"));
        folder.setFolderName(name);
        folder.setDescription(desc);
        return folderRepository.save(folder);
    }

    @Transactional
    public void deleteFolder(Long folderId) {
        if (!folderRepository.existsById(folderId)) {
            throw new RuntimeException("Folder không tồn tại!");
        }
        List<Document> docs = documentRepository.findByFolderId(folderId);
        for (Document doc : docs) {
            fileStorageService.deleteFileFromDisk(doc.getFilePath());
        }
        folderRepository.deleteById(folderId);
    }
}