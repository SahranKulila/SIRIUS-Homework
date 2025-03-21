package io.kulila.client;

import com.fasterxml.jackson.databind.JsonNode;
import io.kulila.client.ClientFX;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;

public class ClientTest extends Application {

    private final ClientFX client = new ClientFX();

    @Override
    public void start(Stage primaryStage) {
        client.start();

        runSync(() -> {
            sleep(1000);
            testSignup();
        });
    }

    private void testSignup() {
        System.out.println("-> SIGNUP");
        client.signup("testuser", "testpass", response -> {
            printResponse("SIGNUP", response);
            testLogin();
        });
    }

    private void testLogin() {
        System.out.println("-> LOGIN");
        client.login("testuser", "testpass", response -> {
            printResponse("LOGIN", response);
            testCreateProject();
        });
    }

    private void testCreateProject() {
        System.out.println("-> CREATE_PROJECT");
        client.createProject("Test Project", response -> {
            printResponse("CREATE_PROJECT", response);
            testGetProjects();
        });
    }

    private void testGetProjects() {
        System.out.println("-> GET_PROJECTS");
        client.getProjects(response -> {
            printResponse("GET_PROJECTS", response);
            testUpdateProject(); // assumes ID = 1
        });
    }

    private void testUpdateProject() {
        System.out.println("-> UPDATE_PROJECT");
        client.updateProject(1, "Updated Project Name", response -> {
            printResponse("UPDATE_PROJECT", response);
            testDeleteProject(); // assumes ID = 1
        });
    }

    private void testDeleteProject() {
        System.out.println("-> DELETE_PROJECT");
        client.deleteProject(1, response -> {
            printResponse("DELETE_PROJECT", response);
            exit();
        });
    }

    private void printResponse(String label, JsonNode response) {
        System.out.println("[" + label + "] Status: " + response.get("status").asText());
        System.out.println("[" + label + "] Message: " + response.get("message").asText());
        System.out.println("[" + label + "] Data: " + response.get("data"));
        System.out.println("--------------------------------------------------");
    }

    private void runSync(Runnable task) {
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            task.run();
            latch.countDown();
        }).start();
        try {
            latch.await();
        } catch (InterruptedException ignored) {}
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }

    private void exit() {
        client.stop();
        Platform.exit();
        System.out.println("Test completed.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
