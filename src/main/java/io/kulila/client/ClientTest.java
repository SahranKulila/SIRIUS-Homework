package io.kulila.client;

import com.fasterxml.jackson.databind.JsonNode;

public class ClientTest {

    public static void main(String[] args) {
        ClientFX client = new ClientFX();
        client.start();

        waitForConnection();

        test("SIGNUP", client.signup("testuser", "testpass"));
        test("LOGIN", client.login("testuser", "testpass"));
        test("CREATE_PROJECT", client.createProject("Test Project"));
        test("GET_PROJECTS", client.getProjects());
        test("UPDATE_PROJECT", client.updateProject(1, "Updated Project Name"));
        test("DELETE_PROJECT", client.deleteProject(1));

        client.stop();
        System.out.println("Client test completed.");
    }

    private static void test(String label, JsonNode response) {
        System.out.println("-> " + label);
        System.out.println("Status: " + response.get("status").asText());
        System.out.println("Message: " + response.get("message").asText());
        if (response.has("data") && response.get("data") != null && !response.get("data").isNull()) {
            System.out.println("Data: " + response.get("data").toPrettyString());
        }
        System.out.println();
    }

    private static void waitForConnection() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}
    }
}
