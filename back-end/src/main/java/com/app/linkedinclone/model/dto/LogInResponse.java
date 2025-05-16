package com.app.linkedinclone.model.dto;

import com.app.linkedinclone.model.dao.Role;
import lombok.Data;

@Data
public class LogInResponse {
    private String token;
    private String userName;
    private String email;
    private Role userRole;
}
