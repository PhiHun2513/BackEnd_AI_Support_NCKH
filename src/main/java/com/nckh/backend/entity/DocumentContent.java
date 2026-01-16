package com.nckh.backend.entity;

import jakarta.persistence.*;
import lombok.*; // <--- QUAN TRỌNG

@Entity
@Table(name = "document_contents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "extracted_text", columnDefinition = "LONGTEXT")
    private String extractedText;

    @OneToOne
    @JoinColumn(name = "document_id")
    private Document document;
}