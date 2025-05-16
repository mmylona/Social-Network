package com.app.linkedinclone.model.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String contentType;
    @Lob
    private byte[] data;
    private Long userId;
    public FileDocument(String fileName, String contentType, byte[] data,Long userId) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.data = data;
        this.userId = userId;
    }
}
