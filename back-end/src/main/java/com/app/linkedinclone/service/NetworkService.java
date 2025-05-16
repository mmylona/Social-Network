package com.app.linkedinclone.service;

import com.app.linkedinclone.model.dto.ConnectionResponse;
import com.app.linkedinclone.model.dto.Profile;
import com.app.linkedinclone.model.enums.ConnectionStatus;

import java.util.List;

public interface NetworkService {
    List<ConnectionResponse> retrieveConnections(String email);
    List<ConnectionResponse> searchConnections(String currentUserEmail,String userName);
    ConnectionStatus connect(String email, Long userIdToConnect);
    Profile getProfile(String currentEmail,Long requestedId);
    List<ConnectionResponse> getRequestedConnection(String email);
    void updateConnectionRequest(String email, Long userId, ConnectionStatus connectionStatus);
    void removeConnection(String email, Long userId);
    Profile getProfileByChatId(String currentUser,Long chatId);
}
