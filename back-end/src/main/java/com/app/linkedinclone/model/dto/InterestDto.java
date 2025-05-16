package com.app.linkedinclone.model.dto;

import com.app.linkedinclone.model.dao.Interest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InterestDto {
    private Long id;
    private String name;
    private String description;

    public static InterestDto mapToInterestDto(Interest interest) {
        return new InterestDto(interest.getId(), interest.getName(), interest.getDescription());
    }
}
