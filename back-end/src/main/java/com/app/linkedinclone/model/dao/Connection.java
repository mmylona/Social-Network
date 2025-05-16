package com.app.linkedinclone.model.dao;

import com.app.linkedinclone.model.enums.ConnectionStatus;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity(name = "connections_table")
@Getter
@Setter
public class Connection {
    @EmbeddedId
    private ConnectionId connectionId;
    @Enumerated(EnumType.STRING)
    private ConnectionStatus connectionStatus;

    public static List<Connection> generateConnections(List<User> users) {
        List<Connection> connections = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < users.size(); i++) {
            for (int j = i + 1; j < users.size(); j++) {
                if (random.nextBoolean()) { // Randomly decide whether to connect two users
                    Connection connection = new Connection();
                    ConnectionId connectionId = new ConnectionId();
                    connectionId.setUser1Id(users.get(i).getId());
                    connectionId.setUser2Id(users.get(j).getId());
                    connection.setConnectionId(connectionId);
                    connection.setConnectionStatus(ConnectionStatus.CONNECTED); // Set the connection status
                    connections.add(connection);
                }
            }
        }
        return connections;
    }
}
