package com.app.linkedinclone.model.dto;

import com.app.linkedinclone.model.enums.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoiceNoteResponse {
    private String message;
    private String voiceNoteUri;
    private Long voiceNoteId;
}
