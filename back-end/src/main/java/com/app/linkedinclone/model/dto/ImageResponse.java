package com.app.linkedinclone.model.dto;

import com.app.linkedinclone.model.enums.ResponseStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ImageResponse {
    private ResponseStatus status;
    private String message;
    private String imageUri;
    private Long imageId;
}
