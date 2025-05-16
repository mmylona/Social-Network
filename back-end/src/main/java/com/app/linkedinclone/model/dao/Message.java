package com.app.linkedinclone.model.dao;

import com.app.linkedinclone.model.dto.MessageDto;
import com.app.linkedinclone.service.ImageService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User recipient;
    @ManyToOne
    private Chat chat;
    private String content;

    private LocalDateTime timestamp;

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", sender.email=" + sender.getEmail() +
                ", recipient.email=" + recipient.getEmail() +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
    public static MessageDto convertToDto(Message message, ImageService imageService) {
        MessageDto messageDto = new MessageDto();
        messageDto.setChatId(message.getChat().getId());
        messageDto.setSenderEmail(message.getSender().getEmail());
        messageDto.setSenderName(message.getSender().getFirstName() + " " + message.getSender().getLastName());
        messageDto.setRecipientEmail(message.getRecipient().getEmail());
        messageDto.setRecipientName(message.getRecipient().getFirstName() + " " + message.getRecipient().getLastName());
        messageDto.setContent(message.getContent());
        messageDto.setTimestamp(message.getTimestamp());
        messageDto.setRecipientFirstName(message.getRecipient().getFirstName());
        messageDto.setRecipientLastName(message.getRecipient().getLastName());
        messageDto.setSenderFirstName(message.getSender().getFirstName());
        messageDto.setSenderLastName(message.getSender().getLastName());
        messageDto.setSenderProfilePicUrl(imageService.getProfileImageUrl(message.getSender().getEmail()));
        messageDto.setRecipientProfilePicUrl(imageService.getProfileImageUrl(message.getRecipient().getEmail()));
        messageDto.setRecipientId(message.getRecipient().getId());

        return messageDto;

    }
}