package com.app.linkedinclone.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationResponse {
    private String message;
    private String email;
    private String name;
    private String token;

}
