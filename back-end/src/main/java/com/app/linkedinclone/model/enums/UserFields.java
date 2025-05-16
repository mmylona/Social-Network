package com.app.linkedinclone.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserFields {
    CV("cv"),
    POSTS("posts"),
    EXPERIENCE("experience"),
    NOTES("notes"),
    COMMENTS("comments"),
    NETWORK("network"),
    EMAIL("email"),
    BIO("bio"),
    PHONE("phone"),
    ADDRESS("address"),
    TITLE("title"),
    CONNECTIONS("connections"),
    EDUCATION("education"),
    FIRSTNAME("firstName"),
    LASTNAME("lastName"),
    SKILLS("skills");
    private String fieldName;


}