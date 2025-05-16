package com.app.linkedinclone.model.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "comments_table")
@NoArgsConstructor

public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    @ManyToOne
    private User author;
    @ManyToOne
    private Post article;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    public Comment(String content, User author, Post article, LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.content = content;
        this.author = author;
        this.article = article;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
}
