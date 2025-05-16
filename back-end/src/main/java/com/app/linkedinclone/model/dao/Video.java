package com.app.linkedinclone.model.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Long postId;
    @Lob
    private String videoUrl;
    @Lob
    private byte[] content;
    private Long userId;
}
