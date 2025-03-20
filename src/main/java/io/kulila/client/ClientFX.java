package io.kulila.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.kulila.utils.YamlConfigurator;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientFX {
    private static final Logger logger = LoggerFactory.getLogger(ClientFX.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

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

            new Thread(this::listenForMessages).start();
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

    public void sendJsonRequestFX(String operation, Object data, ResponseHandler callback) {
        executorService.submit(() -> {
            try {
                String jsonResponse = sendJsonRequest(operation, data);
                JsonNode responseNode = objectMapper.readTree(jsonResponse);
                Platform.runLater(() -> callback.handle(responseNode));
            } catch (Exception e) {
                logger.error("Error processing JSON response: {}", e.getMessage());
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error processing request."));
            }
        });
    }

    public String sendJsonRequest(String operation, Object data) {
        try {
            if (output == null || input == null) {
                return "{\"status\":\"ERROR\", \"message\":\"Client not connected\"}";
            }
            Map<String, Object> request = Map.of("operation", operation, "data", data);
            output.println(objectMapper.writeValueAsString(request));
            return input.readLine();
        } catch (IOException e) {
            return "{\"status\":\"ERROR\", \"message\":\"Failed to send request\"}";
        }
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.show();
    }

    private String configString() {
        return "ClientFX{" +
                "serverHost='" + serverHost + '\'' +
                ", serverPort=" + serverPort +
                '}';
    }

    @FunctionalInterface
    public interface ResponseHandler {
        void handle(JsonNode response);
    }

    public static void main(String[] args) {
        ClientFX client = new ClientFX();
        client.start();
    }
}
