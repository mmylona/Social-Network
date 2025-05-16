package com.app.linkedinclone.model.dao;

import com.app.linkedinclone.model.enums.InteractionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserItemInteraction {

    private Long id;

    private User user;

    private Post post;

    private LocalDateTime interactionTime;
    private InteractionType interactionType;

    public UserItemInteraction(User user, Post post, InteractionType interactionType) {
        this.user = user;
        this.post = post;
        this.interactionType = interactionType;
        this.interactionTime = LocalDateTime.now();
    }

}
