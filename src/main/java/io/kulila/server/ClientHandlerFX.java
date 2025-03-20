package io.kulila.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kulila.database.ConnectionPool;
import io.kulila.database.QueryExecutor;
import io.kulila.database.UserDAO;
import io.kulila.dataclass.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class ClientHandlerFX extends ClientHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandlerFX.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final UserDAO userDAO = new UserDAO();

    public ClientHandlerFX(Socket clientSocket,
                           ConnectionPool connectionPool,
                           QueryExecutor queryExecutor,
                           Map<Class<?>, List<Object>> storedObjects) {
        super(clientSocket, connectionPool, queryExecutor, storedObjects);
    }

    @Override
    public void run() {
        super.run();
    }

    protected void processJsonRequest(String jsonMessage) {
        try {
            JsonNode requestNode = objectMapper.readTree(jsonMessage);
            String operation = requestNode.get("operation").asText();
            JsonNode dataNode = requestNode.get("data");

            switch (operation) {
                case "SIGNUP" -> handleSignup(dataNode);
                default -> sendJsonResponse("ERROR", "Unknown operation", null);
            }
        } catch (Exception e) {
            sendJsonResponse("ERROR", "Invalid JSON format", null);
        }
    }

    private void handleSignup(JsonNode dataNode) {
        String username = dataNode.get("username").asText();
        String password = dataNode.get("password").asText();

        User newUser = new User(username, password);
        boolean success = userDAO.insertUser(newUser);
        sendJsonResponse(success ? "SUCCESS" : "ERROR", success ? "Account created" : "Signup failed", null);
    }

    protected void sendJsonResponse(String status, String message, Object data) {
        try {
            Map<String, Object> response = Map.of(
                    "status", status,
                    "message", message,
                    "data", data
            );
            output.println(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            logger.error("Failed to send JSON response: {}", e.getMessage());
        }
    }
}
