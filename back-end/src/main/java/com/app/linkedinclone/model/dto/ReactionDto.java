package com.app.linkedinclone.model.dto;

import com.app.linkedinclone.model.dao.Reaction;
import com.app.linkedinclone.model.enums.ReactionType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ReactionDto {
    private Long id;
    private Long postId;
    private String authorName;
    private ReactionType reactionType;
    private Long authorId;
    private LocalDateTime createdDate;

    public static ReactionDto mapToReactionDto(Reaction reaction) {
        ReactionDto reactionDto = new ReactionDto();
        reactionDto.setId(reaction.getId());
        reactionDto.setPostId(reaction.getArticle().getId());
        reactionDto.setAuthorName(reaction.getAuthor().getFirstName()+" "+reaction.getAuthor().getLastName());
        reactionDto.setReactionType(reaction.getReactionType());
        reactionDto.setAuthorId(reaction.getAuthor().getId());
        reactionDto.setCreatedDate(reaction.getReactionTime());
        return reactionDto;
    }
}
