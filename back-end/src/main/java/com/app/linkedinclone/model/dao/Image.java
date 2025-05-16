package com.app.linkedinclone.model.dao;

import com.app.linkedinclone.model.enums.ImageType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
public class Image implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String name;
    @Enumerated(EnumType.STRING)
    private ImageType type;
    @Lob
    private String imageUri;
    @Lob
    private byte[] picByte;


}
