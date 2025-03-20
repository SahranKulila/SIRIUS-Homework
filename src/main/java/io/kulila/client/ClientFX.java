package io.kulila.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientFX extends Client {
    private static final Logger logger = LoggerFactory.getLogger(ClientFX.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public ClientFX() {
        super();
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
            Map<String, Object> request = Map.of("operation", operation, "data", data);
            getOutput().println(objectMapper.writeValueAsString(request));
            return getInput().readLine();
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

    @FunctionalInterface
    public interface ResponseHandler {
        void handle(JsonNode response);
    }
}
