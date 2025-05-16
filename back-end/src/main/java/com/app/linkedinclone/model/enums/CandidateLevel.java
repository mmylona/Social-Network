package com.app.linkedinclone.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CandidateLevel {
    JUNIOR("Junior"),
    MID_LEVEL("Mid-level"),
    SENIOR("Senior"),
    DIRECTOR("Director"),
    EXECUTIVE("Executive");

    private final String value;
}
