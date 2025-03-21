package io.kulila.server;

import com.fasterxml.jackson.databind.JsonNode;
import io.kulila.client.ClientFX;

import java.io.IOException;
import java.net.Socket;

public class TestClientServerFX {

    public static void main(String[] args) {
        Thread serverThread = new Thread(() -> {
            ServerFX server = new ServerFX();
            server.start();
        });
        serverThread.setDaemon(true);
        serverThread.start();

        waitForServer(8080, 5000);

        ClientFX client = new ClientFX();
        client.start();
        waitForConnection();

        runTest("SIGNUP", client.signup("testuser_fx", "testpass"));
        runTest("LOGIN", client.login("testuser_fx", "testpass"));
        runTest("CREATE_PROJECT", client.createProject("FX Project"));
        runTest("GET_PROJECTS", client.getProjects());
        runTest("UPDATE_PROJECT", client.updateProject(1, "Updated FX Project"));
        runTest("DELETE_PROJECT", client.deleteProject(1));

        client.stop();
        System.out.println("Server + Client test completed.");
    }

    private static void runTest(String label, JsonNode response) {
        System.out.println("[" + label + "]");
        System.out.println("Status: " + response.get("status").asText());
        System.out.println("Message: " + response.get("message").asText());
        if (response.has("data") && response.get("data") != null && !response.get("data").isNull()) {
            System.out.println("Data: " + response.get("data").toPrettyString());
        }
        System.out.println();
    }

    private static void waitForServer(int port, int timeoutMillis) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            try (Socket ignored = new Socket("localhost", port)) {
                return;
            } catch (IOException ignored) {
                try { Thread.sleep(100); } catch (InterruptedException ignored2) {}
            }
        }
        throw new RuntimeException("Server did not start in time.");
    }

    private static void waitForConnection() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}
    }
}
