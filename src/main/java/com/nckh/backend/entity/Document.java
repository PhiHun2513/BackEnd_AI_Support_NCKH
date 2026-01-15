package com.nckh.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private String filePath;

    private Long fileSize;


    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }

    // 1. Liên kết N-1 với Folder (Nhiều file thuộc 1 thư mục)
    @ManyToOne
    @JoinColumn(name = "folder_id") // Tên cột khóa ngoại trong bảng documents
    private Folder folder;

    // 2. Liên kết 1-1 với Content (1 file có 1 nội dung)
    @OneToOne(mappedBy = "document", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DocumentContent content;
}