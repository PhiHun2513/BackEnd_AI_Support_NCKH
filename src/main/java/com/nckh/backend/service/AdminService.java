package com.nckh.backend.service;

import com.nckh.backend.entity.Folder;
import com.nckh.backend.entity.User;
import com.nckh.backend.repository.DocumentRepository;
import com.nckh.backend.repository.FolderRepository;
import com.nckh.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final DocumentRepository documentRepository;
    private final FolderService folderService;

    public Map<String, Long> getSystemStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.countByRole("USER"));
        stats.put("totalFolders", folderRepository.count());
        stats.put("totalDocuments", documentRepository.count());
        return stats;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUserAndData(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User không tồn tại");
        }
        List<Folder> userFolders = folderRepository.findByUserId(userId);

        for (Folder folder : userFolders) {
            folderService.deleteFolder(folder.getId());
        }

        userRepository.deleteById(userId);
    }
}