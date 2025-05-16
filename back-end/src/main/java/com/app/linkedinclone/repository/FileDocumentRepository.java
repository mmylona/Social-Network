package com.app.linkedinclone.repository;

import com.app.linkedinclone.model.dao.FileDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileDocumentRepository extends JpaRepository<FileDocument, Long> {
    Optional<FileDocument> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}