package com.app.linkedinclone.service;

import com.app.linkedinclone.model.dao.FileDocument;
import com.app.linkedinclone.model.dao.User;
import com.app.linkedinclone.repository.FileDocumentRepository;
import com.app.linkedinclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class FileStorageService {


    private final FileDocumentRepository fileDocumentRepository;
    private final UserRepository userRepository;

    public void storeFile(MultipartFile file, Long userId) throws IOException {

        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        byte[] data = file.getBytes();
        FileDocument fileDocument = new FileDocument(fileName, contentType, data, userId);
        fileDocumentRepository.save(fileDocument);

    }

    public FileDocument getFile(Long userId) {
        return fileDocumentRepository.findByUserId(userId).orElse(null);
    }

    public void updateFile(Long userId, MultipartFile file) throws IOException {

        FileDocument fileDocument = fileDocumentRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("File not found with user id " + userId));
        fileDocument.setFileName(file.getOriginalFilename());
        fileDocument.setContentType(file.getContentType());
        fileDocument.setData(file.getBytes());
        fileDocumentRepository.save(fileDocument);

    }

    public ResponseEntity<ByteArrayResource> getCv(String email) {

        try {
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                FileDocument fileDocument = fileDocumentRepository.findByUserId(user.get().getId()).orElseThrow(() -> new RuntimeException("File not found with user id " + user.get().getId()));
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(fileDocument.getContentType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileDocument.getFileName() + "\"")
                        .body(new ByteArrayResource(fileDocument.getData()));
            }
        } catch (Exception e) {
            log.error("Error while downloading file : {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);

    }

}