package com.app.linkedinclone.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchResult {
    private String email;
    private String firstName;
    private String lastName;
    private String title;
    private String profilePicUrl;
}
