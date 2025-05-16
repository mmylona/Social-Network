package com.app.linkedinclone.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReactionType {
    LIKE("LIKE"),
    LOVE("LOVE"),
    CARE("CARE");

    private final String reaction;
}
