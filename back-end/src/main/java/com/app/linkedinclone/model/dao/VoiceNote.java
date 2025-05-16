package com.app.linkedinclone.model.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class VoiceNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Lob
    private String voiceNoteUri;
    private Long userId;
    private Long postId;
    private Long voiceNoteDuration;
    @Lob
    private byte[] content;

}
