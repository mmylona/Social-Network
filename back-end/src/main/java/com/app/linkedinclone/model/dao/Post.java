package com.app.linkedinclone.model.dao;

import com.app.linkedinclone.model.dto.UserInteraction;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.app.linkedinclone.model.dto.CommentDto.mapToCommentDto;
import static com.app.linkedinclone.model.dto.ReactionDto.mapToReactionDto;

@Getter
@Setter
@Entity
@Slf4j
@Table(name = "posts_table")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    @ManyToOne
    @JsonBackReference
    private User author;
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reaction> reactions;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public Post() {
        this.comments = new ArrayList<>();
        this.reactions = new ArrayList<>();
    }

    private Long videoId;
    private Long imageId;
    private Long voiceNoteId;
    private Integer postViews;

    public List<UserInteraction> getUserInteractions() {
        List<UserInteraction> userInteractions = new ArrayList<>();

        for (Reaction reaction : this.reactions) {
            log.debug("reaction: {}", reaction);
            UserInteraction userInteraction = UserInteraction.builder()
                    .userName(reaction.getAuthor().getUsername())
                    .reaction(mapToReactionDto(reaction))
                    .postId(this.id)
                    .build();

            log.debug("userInteraction: {}", userInteraction);
            userInteractions.add(userInteraction);
        }

        for (Comment comment : this.comments) {
            UserInteraction userInteraction = UserInteraction.builder()
                    .userName(comment.getAuthor().getUsername())
                    .comment(mapToCommentDto(comment))
                    .postId(this.id)
                    .build();
            userInteractions.add(userInteraction);
        }

        return userInteractions;
    }


}
