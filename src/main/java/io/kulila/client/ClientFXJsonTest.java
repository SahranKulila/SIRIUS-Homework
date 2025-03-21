package io.kulila.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kulila.client.ClientFX;

public class ClientFXJsonTest {

    public static void main(String[] args) {
        ClientFX client = new ClientFX();
        ObjectMapper mapper = client.getObjectMapper();

        System.out.println("---- Test: Valid JSON parsing ----");
        testValidJsonParsing(mapper);

        System.out.println("\n---- Test: Invalid JSON parsing ----");
        testInvalidJsonParsing(mapper);

        System.out.println("\n---- Test: Null-safe request generation ----");
        testSendJsonRequestWithNullIO(client);

        System.out.println("\nAll JSON tests completed.");
    }

    private static void testValidJsonParsing(ObjectMapper mapper) {
        String json = """
            {
              "status": "SUCCESS",
              "message": "All good",
              "data": {
                "id": 1,
                "name": "Test Project"
              }
            }
            """;

        try {
            JsonNode node = mapper.readTree(json);
            System.out.println("Parsed successfully!");
            System.out.println("Status: " + node.get("status").asText());
            System.out.println("Message: " + node.get("message").asText());
            System.out.println("Data (id): " + node.get("data").get("id").asInt());
            System.out.println("Data (name): " + node.get("data").get("name").asText());
        } catch (Exception e) {
            System.err.println("Failed to parse valid JSON: " + e.getMessage());
        }
    }

    private static void testInvalidJsonParsing(ObjectMapper mapper) {
        String invalidJson = "{ invalid json }";

        try {
            mapper.readTree(invalidJson);
            System.err.println("Should not succeed parsing invalid JSON");
        } catch (Exception e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
    }

    private static void testSendJsonRequestWithNullIO(ClientFX client) {
        String response = client.sendJsonRequest("GET_PROJECTS", null);
        System.out.println("Response: " + response);
        if (response.contains("Client not connected")) {
            System.out.println("sendJsonRequest correctly handled null IO streams.");
        } else {
            System.err.println("sendJsonRequest should return error JSON when not connected.");
        }
    }
}
