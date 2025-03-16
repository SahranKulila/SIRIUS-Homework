package io.kulila.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiFunction;

public final class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final List<Class<?>> registeredClasses = new ArrayList<>();
    private static final Map<Class<?>, Map<String, Class<?>>>
            classFieldsCache = new HashMap<>();
    private static final Map<Class<?>, BiFunction<JsonNode, Class<?>, Boolean>>
            customTypeMatchers = new HashMap<>();

    private JsonUtils() {
        throw new UnsupportedOperationException(
                "JsonUtils is a utility class and cannot be instantiated");
    }

    public static void registerClass(Class<?> clazz) {
        registeredClasses.add(clazz);
        cacheClassFields(clazz);
    }

    public static void registerCustomTypeMatcher(Class<?> clazz,
                                                 BiFunction<JsonNode, Class<?>, Boolean> matcher) {
        customTypeMatchers.put(clazz, matcher);
    }

    public static String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Serialization error: {}", e.getMessage());
            throw new RuntimeException("Failed to serialize object to JSON.", e);
        }
    }

    public static void serializeToFile(Object object, String filePath) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), object);
        } catch (IOException e) {
            logger.error("Serialization to file error: {}", e.getMessage());
            throw new RuntimeException("Failed to serialize object to file.", e);
        }
    }

    public static void serializeToFileDynamic(Object object, String directoryPath) {
        String fileName = object.getClass().getSimpleName() + ".json";
        String fullPath = Paths.get(directoryPath, fileName).toString();
        if (Files.exists(Paths.get(fullPath))) {
            String msg = "File already exists: " + fullPath;
            logger.error(msg);
            throw new RuntimeException(msg);
        }
        serializeToFile(object, fullPath);
    }

    public static <T> T deserializeFromString(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            logger.error("Deserialization error: {}", e.getMessage());
            throw new RuntimeException("Failed to deserialize JSON string to " + clazz.getName(), e);
        }
    }

    public static <T> T deserializeFromString(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            logger.error("Deserialization with type reference error: {}", e.getMessage());
            throw new RuntimeException("Failed to deserialize JSON string (generic type).", e);
        }
    }

    public static <T> T deserializeFromFile(String filePath, Class<T> clazz) {
        try {
            return objectMapper.readValue(new File(filePath), clazz);
        } catch (IOException e) {
            logger.error("File reading/deserialization error: {}", e.getMessage());
            throw new RuntimeException("Failed to read JSON from file.", e);
        }
    }

    public static <T> T deserializeFromFile(String filePath, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(new File(filePath), typeReference);
        } catch (IOException e) {
            logger.error("Deserialization from file (generic type) error: {}", e.getMessage());
            throw new RuntimeException("Failed to deserialize JSON from file (generic type).", e);
        }
    }

    public static Object deserializeFromStringDynamic(String json) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(json);
        return deserialize(jsonNode);
    }

    public static Object deserializeFromFileDynamic(String filePath) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(new File(filePath));
        return deserialize(jsonNode);
    }

    private static Object deserialize(JsonNode jsonNode) throws JsonProcessingException {
        for (Class<?> clazz : classFieldsCache.keySet()) {
            if (matchesClassStructure(jsonNode, clazz)) {
                return objectMapper.treeToValue(jsonNode, clazz);
            }
        }
        throw new IllegalArgumentException("No matching class found for JSON structure.");
    }

    private static boolean matchesClassStructure(JsonNode jsonNode, Class<?> clazz) {
        if (!jsonNode.isObject()) {
            return false;
        }

        Map<String, Class<?>> classFields = getClassFields(clazz);

        for (String fieldName : classFields.keySet()) {
            if (!jsonNode.has(fieldName)) {
                return false;
            }
            JsonNode fieldValue = jsonNode.get(fieldName);
            Class<?> expectedType = classFields.get(fieldName);

            if (!isTypeMatch(fieldValue, expectedType)) {
                return false;
            }
        }
        return true;
    }

    private static Map<String, Class<?>> getClassFields(Class<?> clazz) {
        return classFieldsCache.computeIfAbsent(clazz, c -> {
            Map<String, Class<?>> fields = new HashMap<>();
            for (Field field : c.getDeclaredFields()) {
                fields.put(field.getName(), field.getType());
            }
            return fields;
        });
    }

    private static void cacheClassFields(Class<?> clazz) {
        getClassFields(clazz);
    }

    private static boolean isTypeMatch(JsonNode jsonNode, Class<?> expectedType) {
        if (customTypeMatchers.containsKey(expectedType)) {
            return customTypeMatchers.get(expectedType).apply(jsonNode, expectedType);
        }

        if (jsonNode.isTextual() && expectedType == String.class) {
            return true;
        } else if (jsonNode.isInt() && (expectedType == int.class || expectedType == Integer.class)) {
            return true;
        } else if (jsonNode.isBoolean() && (expectedType == boolean.class || expectedType == Boolean.class)) {
            return true;
        } else if (jsonNode.isLong() && (expectedType == long.class || expectedType == Long.class)) {
            return true;
        } else if (jsonNode.isDouble() && (expectedType == double.class || expectedType == Double.class)) {
            return true;
        } else if (jsonNode.isObject() && !expectedType.isPrimitive()) {
            return matchesClassStructure(jsonNode, expectedType);
        } else if (jsonNode.isArray()) {
            return expectedType.isArray() || Collection.class.isAssignableFrom(expectedType);
        }
        return false;
    }
}
