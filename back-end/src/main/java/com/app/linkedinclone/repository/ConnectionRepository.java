package com.app.linkedinclone.repository;

import com.app.linkedinclone.model.dao.Connection;
import com.app.linkedinclone.model.dao.ConnectionId;
import com.app.linkedinclone.model.enums.ConnectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;
public interface ConnectionRepository extends JpaRepository<Connection, ConnectionId> {
    @Query("SELECT c FROM connections_table c WHERE (c.connectionId.requestUserId = :currentUserId AND c.connectionId.responseUserId = :userToConnectId) OR (c.connectionId.requestUserId = :userToConnectId AND c.connectionId.responseUserId = :currentUserId)")
    Optional<Connection> findConnectionByUsers(@Param("currentUserId") Long currentUserId, @Param("userToConnectId") Long userToConnectId);
    List<Connection> findAllByConnectionId_ResponseUserIdAndConnectionStatus(Long userId, ConnectionStatus connectionStatus);
}
