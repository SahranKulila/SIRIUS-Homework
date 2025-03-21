package io.kulila.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kulila.database.ConnectionPool;
import io.kulila.database.ProjectDAO;
import io.kulila.database.QueryExecutor;
import io.kulila.database.UserDAO;
import io.kulila.dataclass.Project;
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
                case "LOGIN" -> handleLogin(dataNode);
                case "GET_PROJECTS" -> handleGetProjects();
                case "CREATE_PROJECT" -> handleCreateProject(dataNode);
                case "UPDATE_PROJECT" -> handleUpdateProject(dataNode);
                case "DELETE_PROJECT" -> handleDeleteProject(dataNode);
                default -> sendJsonResponse("ERROR", "Unknown operation", null);
            }
        } catch (Exception e) {
            sendJsonResponse("ERROR", "Invalid JSON format", null);
        }
    }

    private void handleSignup(JsonNode dataNode) {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            UserDAO userDAO = new UserDAO(connection);
            String username = dataNode.get("username").asText();
            String password = dataNode.get("password").asText();
            boolean success = userDAO.insertUser(new User(username, password));
            sendJsonResponse(success ? "SUCCESS" : "ERROR", success ? "Account created" : "Signup failed", null);
        } catch (Exception e) {
            sendJsonResponse("ERROR", "Signup failed", null);
        } finally {
            if (connection != null) connectionPool.releaseConnection(connection);
        }
    }

    private void handleLogin(JsonNode dataNode) {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            UserDAO userDAO = new UserDAO(connection);
            String username = dataNode.get("username").asText();
            String password = dataNode.get("password").asText();
            boolean valid = userDAO.validateUser(username, password);
            sendJsonResponse(valid ? "SUCCESS" : "ERROR", valid ? "Login successful" : "Invalid credentials", null);
        } catch (Exception e) {
            sendJsonResponse("ERROR", "Login failed", null);
        } finally {
            if (connection != null) connectionPool.releaseConnection(connection);
        }
    }

    private void handleGetProjects() {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            ProjectDAO projectDAO = new ProjectDAO(connection);
            List<Project> projects = projectDAO.getAllProjects();
            sendJsonResponse("SUCCESS", "Projects retrieved", projects);
        } catch (Exception e) {
            sendJsonResponse("ERROR", "Failed to retrieve projects", null);
        } finally {
            if (connection != null) connectionPool.releaseConnection(connection);
        }
    }

    private void handleCreateProject(JsonNode dataNode) {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            ProjectDAO projectDAO = new ProjectDAO(connection);
            String name = dataNode.get("name").asText();
            Project project = projectDAO.createProject(name);
            sendJsonResponse(project != null ? "SUCCESS" : "ERROR",
                    project != null ? "Project created" : "Failed to create project",
                    project);
        } catch (Exception e) {
            sendJsonResponse("ERROR", "Failed to create project", null);
        } finally {
            if (connection != null) connectionPool.releaseConnection(connection);
        }
    }

    private void handleUpdateProject(JsonNode dataNode) {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            ProjectDAO projectDAO = new ProjectDAO(connection);
            int id = dataNode.get("id").asInt();
            String name = dataNode.get("name").asText();
            Project project = new Project(id, name, "");
            boolean updated = projectDAO.updateProject(project);
            sendJsonResponse(updated ? "SUCCESS" : "ERROR", updated ? "Project updated" : "Update failed", null);
        } catch (Exception e) {
            sendJsonResponse("ERROR", "Failed to update project", null);
        } finally {
            if (connection != null) connectionPool.releaseConnection(connection);
        }
    }

    private void handleDeleteProject(JsonNode dataNode) {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            ProjectDAO projectDAO = new ProjectDAO(connection);
            int id = dataNode.get("id").asInt();
            boolean deleted = projectDAO.deleteProject(id);
            sendJsonResponse(deleted ? "SUCCESS" : "ERROR", deleted ? "Project deleted" : "Delete failed", null);
        } catch (Exception e) {
            sendJsonResponse("ERROR", "Failed to delete project", null);
        } finally {
            if (connection != null) connectionPool.releaseConnection(connection);
        }
    }

    private void sendJsonResponse(String status, String message, Object data) {
        if (output == null) {
            logger.error("Output stream is null. Cannot send response.");
            return;
        }

        try {
            Map<String, Object> response = Map.of(
                    "status", status,
                    "message", message,
                    "data", data
            );
            String json = objectMapper.writeValueAsString(response);
            logger.info("Sending response: {}", json);
            output.println(json);
            logger.info("Response sent.");
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
