package com.app.linkedinclone.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MessageDto {
    private Long chatId;
    private String senderEmail;
    private String senderName;
    private String senderFirstName;
    private String senderLastName;
    private String recipientEmail;
    private String recipientName;
    private String recipientFirstName;
    private String recipientLastName;
    private String senderProfilePicUrl;
    private String recipientProfilePicUrl;
    private String content;
    private Long recipientId;
    private LocalDateTime timestamp;

    @Override
    public String toString() {
        return "MessageDto{" +
                "senderEmail='" + senderEmail + '\'' +
                ", senderName='" + senderName + '\'' +
                ", senderFirstName='" + senderFirstName + '\'' +
                ", senderLastName='" + senderLastName + '\'' +
                ", recipientEmail='" + recipientEmail + '\'' +
                ", recipientName='" + recipientName + '\'' +
                ", recipientFirstName='" + recipientFirstName + '\'' +
                ", recipientLastName='" + recipientLastName + '\'' +
//                ", senderProfilePicUrl='" + senderProfilePicUrl + '\'' +
//                ", recipientProfilePicUrl='" + recipientProfilePicUrl + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}