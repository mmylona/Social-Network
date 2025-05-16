package com.app.linkedinclone.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class UserInteraction {
    private String userName;
    private ReactionDto reaction;
    private CommentDto comment;
    private Long postId;
}
