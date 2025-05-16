package com.app.linkedinclone.controller;

import com.app.linkedinclone.model.dto.ConnectionResponse;
import com.app.linkedinclone.model.dto.Profile;
import com.app.linkedinclone.model.enums.ConnectionStatus;
import com.app.linkedinclone.service.NetworkService;
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
@RequestMapping("/network")
@CrossOrigin(origins = "https://localhost:3000")
public class NetWorkController {
    private final NetworkService networkService;
    @GetMapping("/connections")
    public List<ConnectionResponse> retrieveConnections(@AuthenticationPrincipal UserDetails userDetails) {
        return networkService.retrieveConnections(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    }
    @GetMapping("/search")
    public List<ConnectionResponse> searchConnections(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String query) {
        return networkService.searchConnections(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(),query);
    }
    @PostMapping("/connect/{userId}")
    public ConnectionStatus connect(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long userId) {
        return networkService.connect(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(), userId);
    }

    @GetMapping("/profile/{userId}")
    public Profile getProfile( @PathVariable Long userId) {
        return networkService.getProfile(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(),userId);
    }

    @GetMapping("/profile-chat/{chatId}")
        public Profile getProfileByChatId(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long chatId){
        return networkService.getProfileByChatId(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(),chatId);
    }

    @GetMapping("/requested")
    public List<ConnectionResponse> getRequestedConnection() {
        return networkService.getRequestedConnection(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    }
    @PostMapping("/connection-requests/{requestId}/{status}")
    public void updateConnectionRequest(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long requestId, @PathVariable String status) {
        log.info("Updating connection request with id : {} to status : {}", requestId, status);
        networkService.updateConnectionRequest(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(),requestId, ConnectionStatus.valueOf(status));
    }

    @DeleteMapping("/remove-connection/{userId}")
    public void removeConnection(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long userId) {
        networkService.removeConnection(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(), userId);
    }
}
