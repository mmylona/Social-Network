package com.app.linkedinclone.service;

import com.app.linkedinclone.model.dao.*;
import com.app.linkedinclone.model.dto.ConnectionResponse;
import com.app.linkedinclone.model.dto.Profile;
import com.app.linkedinclone.model.dto.UserDto;
import com.app.linkedinclone.model.enums.ConnectionStatus;
import com.app.linkedinclone.model.enums.ImageType;
import com.app.linkedinclone.repository.ChatRepository;
import com.app.linkedinclone.repository.ConnectionRepository;
import com.app.linkedinclone.repository.ImageRepository;
import com.app.linkedinclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.*;
import java.util.stream.Collectors;

import static com.app.linkedinclone.model.dto.Profile.mapProfile;
import static com.app.linkedinclone.model.enums.ConnectionStatus.ACCEPTED;
import static com.app.linkedinclone.model.enums.ConnectionStatus.CONNECTED;
import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class NetworkServiceImpl implements NetworkService {

    private static final String USER_NOT_FOUND = "User not found";
    private final UserRepository userRepository;
    private final ConnectionRepository connectionRepository;
    private final ImageRepository imageRepository;
    private final ChatRepository chatRepository;

    @Override
    public List<ConnectionResponse> retrieveConnections(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (isNull(user)) {
            throw new UsernameNotFoundException(USER_NOT_FOUND);
        }
        return user.getNetwork().stream()
                .map(
                        userNet ->{
                            Image image=imageRepository.findByUserIdAndTypeIs(userNet.getId(), ImageType.PROFILE);
                            return ConnectionResponse.mapConnectionResponse(userNet,!isNull(image)? image.getImageUri():null);

                        })
                .toList();
    }

    @Override
    public List<ConnectionResponse> searchConnections(String currenUserEmail, String userName) {
        List<User> users = userRepository.findAllByUserNameContainingIgnoreCase(userName);
        return users.stream()
                .filter(user -> !user.getEmail().equals(currenUserEmail))
                .map(userNet -> {
                    Image image = imageRepository.findByUserIdAndTypeIs(userNet.getId(), ImageType.PROFILE);
                    return ConnectionResponse.mapConnectionResponse(userNet, !isNull(image) ? image.getImageUri() : null);
                })
                .toList();
    }

    @Override
    public ConnectionStatus connect(String email, Long userToConnectId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));

        if (hasAlreadyConnected(user, userToConnectId)) {
            return ConnectionStatus.ACCEPTED;
        }

        Long currentUserId = user.getId();
        log.debug("Current user - connect-id: {} User to connect id: {}", currentUserId, userToConnectId);

        return connectionRepository.findConnectionByUsers(currentUserId, userToConnectId)
                .map(connection -> {
                    log.debug("Connection status -connect: {}", connection.getConnectionStatus());
                    return connection.getConnectionStatus();
                })
                .orElseGet(() -> createPendingConnection(currentUserId, userToConnectId));
    }

    private ConnectionStatus createPendingConnection(Long currentUserId, Long userToConnectId) {
        Connection connection = new Connection();
        ConnectionId connectionId = new ConnectionId(currentUserId, userToConnectId);
        connection.setConnectionId(connectionId);
        connection.setConnectionStatus(ConnectionStatus.PENDING);
        connectionRepository.save(connection);
        return ConnectionStatus.PENDING;
    }

    @Override
    public Profile getProfile(String currentEmail, Long requestedId) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));

        log.debug("Current user -getProfile id {}, request user id {}", user.getId(), requestedId);
        ConnectionStatus connectionStatus = connectionRepository.findConnectionByUsers(user.getId(), requestedId).map(Connection::getConnectionStatus).orElse(null);
        log.debug("Connection status-getProfile: {}", connectionStatus);
        User requestedUser = userRepository.findById(requestedId).orElseThrow(() -> new UsernameNotFoundException("Requested user not found"));
        Profile profile = mapProfile(requestedUser);
        if (hasAlreadyConnected(user, requestedId)) {
            profile.setConnectionStatus(CONNECTED);
            profile.setNetwork(requestedUser.getNetwork().stream().map(UserDto::mapToUserDto).collect(Collectors.toSet()));
        } else {
            profile.setConnectionStatus(connectionStatus);
        }
        Image image = imageRepository.findByUserIdAndTypeIs(requestedId, ImageType.PROFILE);
        if (!isNull(image)) {
            profile.setProfilePic(image.getImageUri());
        }
        log.debug("Profile-getProfile: {}", profile);
        return profile;
    }

    @Override
    public List<ConnectionResponse> getRequestedConnection(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
        List<Connection> connectionList = connectionRepository.findAllByConnectionId_ResponseUserIdAndConnectionStatus(user.getId(), ConnectionStatus.PENDING);
        if (connectionList.isEmpty())
            return new ArrayList<>();
        return connectionList.stream()
                .map(connection -> userRepository.findById(connection.getConnectionId().getRequestUserId()).orElse(null))
                .filter(Objects::nonNull)
                .map(userNet -> ConnectionResponse.mapConnectionResponse(userNet, imageRepository.findByUserIdAndTypeIs(userNet.getId(), ImageType.PROFILE).getImageUri()))
                .toList();

    }

    @Override
    public void updateConnectionRequest(String email, Long userId, ConnectionStatus connectionStatus) {
        log.debug("Email: {}, userId: {}, connectionStatus: {}", email, userId, connectionStatus);
        User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));

        Connection connection = connectionRepository.findConnectionByUsers(userId, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Connection not found"));

        connection.setConnectionStatus(connectionStatus);

        if (!ACCEPTED.equals(connectionStatus)) {
            log.debug("Connection request is not accepted. No changes have been made in network.");
            connectionRepository.save(connection);
            return;
        }
        User userToConnect = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User to connect not found"));
        // Initialize networks if null
        if (isEmpty(currentUser.getNetwork()))
            currentUser.setNetwork(new HashSet<>());

        if (isEmpty(userToConnect.getNetwork()))
            userToConnect.setNetwork(new HashSet<>());


        // Add each user to the other's network
        currentUser.getNetwork().add(userToConnect);
        userToConnect.getNetwork().add(currentUser);

        // Save both users to persist the changes
        userRepository.save(currentUser);
        userRepository.save(userToConnect);

        // Delete the connection as it's no longer needed
        connectionRepository.delete(connection);
    }

    @Override
    public void removeConnection(String email, Long userId) {
        log.debug("Removing connection between user {} and user {}", email, userId);
        User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
        User userToDisconnect = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User to disconnect not found"));

        currentUser.getNetwork().remove(userToDisconnect);
        userToDisconnect.getNetwork().remove(currentUser);

        userRepository.save(currentUser);
        userRepository.save(userToDisconnect);

    }

    @Override
    public Profile getProfileByChatId(String currentUser, Long chatId) {
        Optional<Chat> chat= chatRepository.findById(chatId);
        if(chat.isEmpty()){
            throw new RuntimeException("Some error occurred ! ");
        }
        User curUser=userRepository.findByEmail(currentUser).orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
        List<User> otherUser=chat.get().getUsers().stream().filter(user -> !user.equals(curUser)).toList();
        if(otherUser.isEmpty())
            return mapProfile(curUser);
        if(otherUser.size()>1){
            throw new RuntimeException("User List has size greater than one, duplicates user may found");
        }
        return mapProfile(otherUser.get(0));


    }

    private boolean hasAlreadyConnected(User user, Long userToConnectId) {
        Set<User> connections = user.getNetwork();
        log.debug("Connections: {}", connections);
        if (!isNull(connections) && connections.stream().anyMatch(u -> u.getId().equals(userToConnectId))) {
            log.info("User {} is already connected to user {}", user.getId(), userToConnectId);
            return true;
        }
        return false;
    }
}
