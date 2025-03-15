package io.kulila.utils;

import org.yaml.snakeyaml.Yaml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;

public class YamlConfigurator {
    private static final Logger logger = LoggerFactory.getLogger(YamlConfigurator.class);

    public void configure(Object target, String yamlFile) throws ReflectiveOperationException, IOException {
        Yaml yaml = new Yaml();

        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(yamlFile)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("YAML file not found: " + yamlFile);
            }
            Map<String, Object> configMap = yaml.load(inputStream);
            configureObject(target, configMap);
        } catch (Exception e) {
            logger.error("Error loading YAML file: " + yamlFile, e);
            throw e;
        }
    }

    private void configureObject(Object target, Map<String, Object> configMap) throws ReflectiveOperationException {
        Class<?> clazz = target.getClass();

        for (Map.Entry<String, Object> entry : configMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            try {
                Field field = clazz.getDeclaredField(key);
                field.setAccessible(true);

                if (value instanceof Map) {
                    Object nestedObject = field.get(target);
                    if (nestedObject == null) {
                        nestedObject = field.getType().getDeclaredConstructor().newInstance();
                        field.set(target, nestedObject);
                    }
                    configureObject(nestedObject, (Map<String, Object>) value);
                } else {
                    field.set(target, convertValue(field, value));
                }
            } catch (NoSuchFieldException e) {
                logger.warn("Field '{}' not found in class {}", key, clazz.getName());
            } catch (Exception e) {
                logger.error("Error configuring field '{}' in class {}", key, clazz.getName(), e);
                throw e;
            }
        }
    }

    private Object convertValue(Field field, Object value) {
        Class<?> fieldType = field.getType();

        if (fieldType.isAssignableFrom(value.getClass())) {
            return value;
        }

        try {
            if (fieldType == int.class || fieldType == Integer.class) {
                return Integer.parseInt(value.toString());
            } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                return Boolean.parseBoolean(value.toString());
            } else if (fieldType == double.class || fieldType == Double.class) {
                return Double.parseDouble(value.toString());
            } else if (fieldType == long.class || fieldType == Long.class) {
                return Long.parseLong(value.toString());
            } else if (fieldType == String.class) {
                return value.toString();
            }
        } catch (Exception e) {
            logger.error("Failed to convert value: {} to type {}", value, fieldType, e);
            throw new IllegalArgumentException("Failed to convert value: " + value + " to type " + fieldType, e);
        }

        throw new IllegalArgumentException("Unsupported type conversion: " + value + " -> " + fieldType);
    }
}
