package io.kulila.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kulila.utils.YamlConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientFX {
    private static final Logger logger = LoggerFactory.getLogger(ClientFX.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String serverHost;
    private int serverPort;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private boolean running = false;

    public ClientFX() {
        loadConfig("client-config.yaml", "<<ClientFX>>");
    }

    private void loadConfig(String configPath, String debug_inheritance) {
        try {
            YamlConfigurator.configure(this, configPath);
            logger.info("Loaded config for {} {}", debug_inheritance, configString());
        } catch (Exception e) {
            logger.error("Failed to load configuration for {} from {}: {}", debug_inheritance, configPath, e.getMessage());
            this.serverHost = "localhost";
            this.serverPort = 8080;
        }
    }

    public void start() {
        try {
            socket = new Socket(serverHost, serverPort);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            running = true;
            logger.info("Connected to server at {}:{}", serverHost, serverPort);
            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
            //new Thread(this::listenForMessages).start();
        } catch (IOException e) {
            logger.error("Error connecting to server: {}", e.getMessage());
        }
    }

    private void listenForMessages() {
        try {
            String response;
            while ((response = input.readLine()) != null) {
                logger.info("Server: {}", response);
            }
        } catch (IOException e) {
            logger.error("Connection to server lost: {}", e.getMessage());
        }
    }

    public void stop() {
        if (!running) return;
        running = false;
        try {
            if (socket != null) socket.close();
            if (input != null) input.close();
            if (output != null) output.close();
            logger.info("ClientFX stopped.");
        } catch (IOException e) {
            logger.error("Error closing client: {}", e.getMessage());
        }
    }

    public String sendJsonRequest(String operation, Object data) {
        try {
            if (output == null || input == null) {
                logger.error("Client not connected (output/input is null)");
                return "{\"status\":\"ERROR\", \"message\":\"Client not connected\"}";
            }

            Map<String, Object> request = new HashMap<>();
            request.put("operation", operation);
            if (data != null) request.put("data", data);

            String requestJson = objectMapper.writeValueAsString(request);
            output.write(requestJson + "\n");
            output.flush();

            String response = input.readLine();
            if (response == null) {
                logger.error("Server closed connection unexpectedly.");
                return "{\"status\":\"ERROR\", \"message\":\"Server closed connection\"}";
            }

            return response;

        } catch (IOException e) {
            logger.error("Failed to send JSON request: {}", e.getMessage());
            return "{\"status\":\"ERROR\", \"message\":\"Failed to send request\"}";
        }
    }


    public JsonNode signup(String username, String password) {
        Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("password", password);
        return parseResponse(sendJsonRequest("SIGNUP", data));
    }

    public JsonNode login(String username, String password) {
        Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("password", password);
        return parseResponse(sendJsonRequest("LOGIN", data));
    }

    public JsonNode getProjects() {
        return parseResponse(sendJsonRequest("GET_PROJECTS", null));
    }

    public JsonNode createProject(String name) {
        Map<String, Object> data = Map.of("name", name);
        return parseResponse(sendJsonRequest("CREATE_PROJECT", data));
    }

    public JsonNode updateProject(int id, String name) {
        Map<String, Object> data = Map.of("id", id, "name", name);
        return parseResponse(sendJsonRequest("UPDATE_PROJECT", data));
    }

    public JsonNode deleteProject(int id) {
        Map<String, Object> data = Map.of("id", id);
        return parseResponse(sendJsonRequest("DELETE_PROJECT", data));
    }

    private JsonNode parseResponse(String jsonResponse) {
        try {
            return objectMapper.readTree(jsonResponse);
        } catch (IOException e) {
            logger.error("Failed to parse JSON response: {}", e.getMessage());
            throw new RuntimeException("Invalid server response", e);
        }
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    private String configString() {
        return "ClientFX{" +
                "serverHost='" + serverHost + '\'' +
                ", serverPort=" + serverPort +
                '}';
    }

    public static void main(String[] args) {
        ClientFX client = new ClientFX();
        client.start();

        JsonNode response = client.signup("testuser_cli", "pass123");
        System.out.println("Signup Response: " + response.toPrettyString());

        response = client.signup("testuser_cli1", "pass1234");
        System.out.println("Signup Response: " + response.toPrettyString());

        response = client.login("testuser_cli", "pass123");
        System.out.println("Login Response: " + response.toPrettyString());
    }
}
