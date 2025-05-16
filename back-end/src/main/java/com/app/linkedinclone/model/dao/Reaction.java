package com.app.linkedinclone.model.dao;

import com.app.linkedinclone.model.enums.ReactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "reaction_table")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User author;
    @Enumerated(EnumType.STRING)
    private ReactionType reactionType;
    private LocalDateTime reactionTime;
    @ManyToOne
    private Post article;

    public Reaction(User author,ReactionType type, LocalDateTime time, Post article) {
        this.author = author;
        this.reactionType = type;
        this.reactionTime = time;
        this.article = article;

    }
}
