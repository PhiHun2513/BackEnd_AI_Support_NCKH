package com.nckh.backend.repository;

import com.nckh.backend.entity.DocumentContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentContentRepository extends JpaRepository<DocumentContent, Long> {
}