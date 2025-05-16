package com.app.linkedinclone.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InteractionType {
    LIKE("LIKE"),
    LOVE("LOVE"),
    CARE("CARE"),
    COMMENT("COMMENT");

    private final String interactionType;
}