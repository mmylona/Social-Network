package com.app.linkedinclone.model.dto;

import com.app.linkedinclone.model.enums.ApiResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class PostRetrievalResponse {
    private String message;
    private ApiResponseStatus success;
    private List<PostDto> postDto;
    long totalPosts;
    long totalPages;

}
