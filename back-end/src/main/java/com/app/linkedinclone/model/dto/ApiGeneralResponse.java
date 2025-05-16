package com.app.linkedinclone.model.dto;

import com.app.linkedinclone.model.enums.ApiResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@AllArgsConstructor
@Getter
public class ApiGeneralResponse{
    private String message;
    private HttpStatus status;

}
