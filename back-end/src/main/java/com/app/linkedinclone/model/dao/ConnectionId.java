package com.app.linkedinclone.model.dao;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ConnectionId implements Serializable {

    private Long requestUserId;
    private Long responseUserId;

    public void setUser1Id(Long id) {
    }

    public void setUser2Id(Long id) {
    }
}
