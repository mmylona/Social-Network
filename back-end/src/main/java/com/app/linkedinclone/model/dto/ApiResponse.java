package com.app.linkedinclone.model.dto;

import com.app.linkedinclone.model.enums.ApiResponseStatus;

public interface ApiResponse {

    String getMessage();

    ApiResponseStatus getSuccess();

    ApiResponse getApiResponseData();
}
