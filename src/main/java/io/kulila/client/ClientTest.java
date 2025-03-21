package io.kulila.client;

import com.fasterxml.jackson.databind.JsonNode;
import io.kulila.client.ClientFX;

import java.util.concurrent.CountDownLatch;

public class ClientTest {

    public static void main(String[] args) {
        ClientFX client = new ClientFX();
        client.start();

        sleep(1000);

        System.out.println("-> SIGNUP");
        runSyncTest(() -> client.signup("testuser", "testpass", printResponse("SIGNUP")));

        System.out.println("-> LOGIN");
        runSyncTest(() -> client.login("testuser", "testpass", printResponse("LOGIN")));

        System.out.println("-> CREATE_PROJECT");
        runSyncTest(() -> client.createProject("Test Project", printResponse("CREATE_PROJECT")));

        System.out.println("-> GET_PROJECTS");
        runSyncTest(() -> client.getProjects(printResponse("GET_PROJECTS")));

        System.out.println("-> UPDATE_PROJECT");
        runSyncTest(() -> client.updateProject(1, "Updated Project Name", printResponse("UPDATE_PROJECT")));

        System.out.println("-> DELETE_PROJECT");
        runSyncTest(() -> client.deleteProject(1, printResponse("DELETE_PROJECT")));

        client.stop();
        System.out.println("Test completed.");
    }

    private static void runSyncTest(Runnable test) {
        CountDownLatch latch = new CountDownLatch(1);
        Runnable wrapped = () -> {
            test.run();
            sleep(1000);
            latch.countDown();
        };
        new Thread(wrapped).start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static ClientFX.ResponseHandler printResponse(String label) {
        return (JsonNode response) -> {
            System.out.println("[" + label + "] Status: " + response.get("status").asText());
            System.out.println("[" + label + "] Message: " + response.get("message").asText());
            System.out.println("[" + label + "] Data: " + response.get("data"));
            System.out.println("--------------------------------------------------");
        };
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
