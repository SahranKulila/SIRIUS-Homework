package io.kulila.utils;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;

public class YamlConfigurator {

    public void configure(Object target, String yamlFile) throws Exception {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(yamlFile);

        if (inputStream == null) {
            throw new IllegalArgumentException("YAML file not found: " + yamlFile);
        }

        Map<String, Object> configMap = yaml.load(inputStream);
        configureObject(target, configMap);
    }

    private void configureObject(Object target, Map<String, Object> configMap) throws Exception {
        Class<?> clazz = target.getClass();

        for (Map.Entry<String, Object> entry : configMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            Field field;
            try {
                field = clazz.getDeclaredField(key);
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field not found: " + key + " in class " + clazz.getName());
            }

            field.setAccessible(true);

            if (value instanceof Map) {
                Object nestedObject = field.get(target);
                if (nestedObject == null) {
                    nestedObject = field.getType().getDeclaredConstructor().newInstance();
                    field.set(target, nestedObject);
                }
                configureObject(nestedObject, (Map<String, Object>) value);
            } else {
                field.set(target, value);
            }
        }
    }
}
