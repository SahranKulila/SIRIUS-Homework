package io.kulila.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class JsonUtils {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // From any Java object to JSON string
    public static <T> String serialize(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            logger.error("Serialization error: {}", e.getMessage());
            throw new RuntimeException("JSON serialization failed.", e);
        }
    }

    // From JSON string to Java object
    public static <T> T deserialize(String jsonString, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            logger.error("Deserialization error: {}", e.getMessage());
            throw new RuntimeException("JSON deserialization failed.", e);
        }
    }

    // From JSON string to Generic Types
    public static <T> T deserialize(String jsonString, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(jsonString, typeReference);
        } catch (Exception e) {
            logger.error("Generic deserialization error: {}", e.getMessage());
            throw new RuntimeException("JSON generic deserialization failed.", e);
        }
    }

    // From any Java object to JSON file
    public static <T> void writeJsonToFile(String filePath, T object) {
        try {
            objectMapper.writeValue(new File(filePath), object);
        } catch (Exception e) {
            logger.error("Writing JSON to file error: {}", e.getMessage());
            throw new RuntimeException("Failed to write JSON to file.", e);
        }
    }

    // From JSON file to a given type
    public static <T> T readJsonFromFile(String filePath, Class<T> clazz) {
        try {
            return objectMapper.readValue(new File(filePath), clazz);
        } catch (Exception e) {
            logger.error("Reading JSON from file error: {}", e.getMessage());
            throw new RuntimeException("Failed to read JSON from file.", e);
        }
    }
}
