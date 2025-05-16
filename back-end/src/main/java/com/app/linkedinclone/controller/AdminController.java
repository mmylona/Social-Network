package com.app.linkedinclone.controller;

import com.app.linkedinclone.model.dao.User;
import com.app.linkedinclone.model.dto.ExportRequest;
import com.app.linkedinclone.model.dto.UserDto;
import com.app.linkedinclone.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;

    @GetMapping("/users")
    public List<UserDto> getUsers(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Getting all users");
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/export/{format}")
    public Resource exportUsers(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String format, @RequestBody ExportRequest request) {
        log.info("Exporting users in format: {} and fields: {}", format, request.getFields());
        return userService.exportUsers(format, request);
    }

}
