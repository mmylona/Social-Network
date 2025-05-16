package com.app.linkedinclone.controller;

import com.app.linkedinclone.model.dto.UserInteraction;
import com.app.linkedinclone.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "https://localhost:3000")
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/user-interactions")
    public List<UserInteraction> getNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        log.debug("Authentication: {}", authentication);

        return notificationService.getNotifications(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    }

}
