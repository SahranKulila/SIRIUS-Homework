package io.kulila.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class JsonFileHandler {
    private static final Logger logger = LoggerFactory.getLogger(JsonFileHandler.class);
    private static final String DEFAULT_JSON_FILE_PATH = "src/main/java/io/kulila/dataclass/project.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String readJsonFile() {
        try {
            File file = new File(DEFAULT_JSON_FILE_PATH);
            if (!file.exists()) {
                return "Error: JSON file not found at " + DEFAULT_JSON_FILE_PATH;
            }
            return new String(Files.readAllBytes(file.toPath()));
        } catch (Exception e) {
            logger.error("Error reading JSON file: {}", e.getMessage());
            return "Error: Failed to read JSON file.";
        }
    }

    public String sendJsonFile(PrintWriter output) {
        String jsonContent = readJsonFile();
        if (jsonContent.startsWith("Error:")) {
            return jsonContent;
        }

        Map<String, Object> jsonMessage = new HashMap<>();
        jsonMessage.put("command", "JSON_FILE");
        jsonMessage.put("data", jsonContent);

        try {
            String jsonString = objectMapper.writeValueAsString(jsonMessage);
            output.println(jsonString);
            return "JSON file sent to server.";
        } catch (Exception e) {
            logger.error("Error sending JSON file: {}", e.getMessage());
            return "Error: Failed to send JSON file.";
        }
    }
}
