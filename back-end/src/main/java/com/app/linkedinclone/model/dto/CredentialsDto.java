package com.app.linkedinclone.model.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CredentialsDto {

    private String email;
    private String newPassword;
    private String oldPassword;
}
