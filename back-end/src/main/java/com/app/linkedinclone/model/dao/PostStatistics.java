package com.app.linkedinclone.model.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PostStatistics {
    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userViews=0L;
    private Long userComments=0L;
    @ManyToOne
    private Post post;
    @ManyToOne
    private User user;
}
