package io.kulila.server;

import io.kulila.dataclass.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class JsonObjectHandler {
    private static final Logger logger = LoggerFactory.getLogger(JsonObjectHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String sendJsonObject(PrintWriter output, Project project) {
        try {
            Map<String, Object> jsonMessage = new HashMap<>();
            jsonMessage.put("command", "JSON_OBJECT");
            jsonMessage.put("data", project);

            String jsonString = objectMapper.writeValueAsString(jsonMessage);
            output.println(jsonString);
            return "JSON object sent to server.";
        } catch (Exception e) {
            logger.error("Error sending JSON object: {}", e.getMessage());
            return "Error: Failed to send JSON object.";
        }
    }

    public Project parseJsonObject(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, Project.class);
        } catch (Exception e) {
            logger.error("Error parsing JSON object: {}", e.getMessage());
            return null;
        }
    }
}
