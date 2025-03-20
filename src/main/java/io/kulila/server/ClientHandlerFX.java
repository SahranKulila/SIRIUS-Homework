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

public class ClientHandlerFX implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandlerFX.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final Socket clientSocket;
    private final ConnectionPool connectionPool;
    private final QueryExecutor queryExecutor;
    private final Map<Class<?>, List<Object>> storedObjects;
    private final UserDAO userDAO = new UserDAO();

    private BufferedReader input;
    private PrintWriter output;
    private boolean running = false;

    public ClientHandlerFX(Socket clientSocket,
                           ConnectionPool connectionPool,
                           QueryExecutor queryExecutor,
                           Map<Class<?>, List<Object>> storedObjects) {
        this.clientSocket = clientSocket;
        this.connectionPool = connectionPool;
        this.queryExecutor = queryExecutor;
        this.storedObjects = storedObjects;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            running = true;
            logger.info("ClientHandlerFX started for client: {}", clientSocket.getInetAddress());

            String message;
            while (running && (message = input.readLine()) != null) {
                processJsonRequest(message);
            }
        } catch (IOException e) {
            logger.error("Error in client communication: {}", e.getMessage());
        } finally {
            stop();
        }
    }

    private void processJsonRequest(String jsonMessage) {
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
        try {
            String username = dataNode.get("username").asText();
            String password = dataNode.get("password").asText();

            User newUser = new User(username, password);
            boolean success = userDAO.insertUser(newUser);
            sendJsonResponse(success ? "SUCCESS" : "ERROR", success ? "Account created" : "Signup failed", null);
        } catch (Exception e) {
            sendJsonResponse("ERROR", "Invalid signup data", null);
        }
    }

    private void sendJsonResponse(String status, String message, Object data) {
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

    private void stop() {
        running = false;
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (clientSocket != null) clientSocket.close();
            logger.info("ClientHandlerFX stopped.");
        } catch (IOException e) {
            logger.error("Error closing client handler: {}", e.getMessage());
        }
    }
}
