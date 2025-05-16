package com.app.linkedinclone.model.dao;

import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ItemVector {
    private Long itemId;

    private List<Double> factors;

    @OneToOne
    private Post post;

}