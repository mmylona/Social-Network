package com.app.linkedinclone.model.dto;

import com.app.linkedinclone.model.dao.Comment;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CommentDto {
    private Long id;
    private Long postId;
    private String authorName;
    private String content;
    private String profilePicUrl;
    private Long authorId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public static CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setPostId(comment.getArticle().getId());
        commentDto.setAuthorName(comment.getAuthor().getFirstName() + " " + comment.getAuthor().getLastName());
        commentDto.setContent(comment.getContent());
        commentDto.setProfilePicUrl(comment.getAuthor().getProfilePicUrl());
        commentDto.setAuthorId(comment.getAuthor().getId());
        commentDto.setCreatedDate(comment.getCreatedDate());
        commentDto.setUpdatedDate(comment.getUpdatedDate());
        return commentDto;
    }
}
