package com.app.linkedinclone.controller;

import com.app.linkedinclone.model.dto.MessageDto;
import com.app.linkedinclone.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/message")
@CrossOrigin(origins = "https://localhost:3000")
public class MessageController {
    private final MessageService messageService;


    @PostMapping("/send")
    public void sendMessage(@AuthenticationPrincipal UserDetails userDetails, @RequestBody MessageDto message) {
        log.info("Sending message to : {}", message.getRecipientEmail());
        message.setSenderEmail(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        messageService.sendMessage(message);
    }

    @GetMapping("/retrieve")
    public List<MessageDto> retrieveMessages(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(required = false) Boolean latest, @RequestParam(required = false) String recipientEmail) {
        return messageService.retrieveMessages(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(), latest, recipientEmail);
    }
    @PostMapping("/createChat/{recipientId}")
    public Long createChat(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long recipientId) {
        log.info("------------- Create chat ------------------- ");
        return messageService.createChat(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(), recipientId);
    }
}
