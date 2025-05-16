package com.app.linkedinclone.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ConnectionStatus {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    CONNECTED("Connected"),
    REJECTED("Rejected");
    private String status;
}
