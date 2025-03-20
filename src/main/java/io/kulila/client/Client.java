package io.kulila.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.kulila.utils.YamlConfigurator;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    protected String serverHost;
    protected int serverPort;
    protected Socket socket;
    protected BufferedReader input;
    protected PrintWriter output;
    protected Scanner scanner;
    protected boolean running = false;

    public Client() {
        this("client-config.yaml");
    }

    public Client(String configPath) {
        this(configPath, "<<Basic Client>>");
    }

    protected Client(String configPath, String debug_inheritance) {
        try {
            YamlConfigurator.configure(this, configPath);
            logger.info("Loaded config for {} {}",debug_inheritance, configString());
        } catch (Exception e) {
            logger.error("Failed to load configuration for {} from {}: {}", debug_inheritance, configPath, e.getMessage());
            this.serverHost = "localhost";
            this.serverPort = 8080;
        }
    }

    public void start() {
        try {
            socket = new Socket(serverHost, serverPort);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);

            running = true;

            logger.info("Connected to server at {}:{}", serverHost, serverPort);
            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

            new Thread(this::listenForMessages).start();

            while (true) {
                System.out.print("> ");
                String message = scanner.nextLine();

                if ("exit".equalsIgnoreCase(message)) {
                    logger.info("Disconnecting from server...");
                    break;
                }

                output.println(message);
            }
        } catch (IOException e) {
            logger.error("Error connecting to server: {}", e.getMessage());
        } finally {
            stop();
        }
    }

    private void listenForMessages() {
        try {
            String response;
            while ((response = input.readLine()) != null) {
                logger.info("Server: {}", response);
            }
        } catch (IOException e) {
            logger.error("Connection to server lost: {}", e.getMessage());
        }
    }

    public void stop() {
        if (!running) return;
        running = false;
        try {
            if (socket != null) socket.close();
            if (input != null) input.close();
            if (output != null) output.close();
            if (scanner != null) scanner.close();
            logger.info("Client stopped.");
        } catch (IOException e) {
            logger.error("Error closing client: {}", e.getMessage());
        }
    }

    protected PrintWriter getOutput() {
        return output;
    }

    protected BufferedReader getInput() {
        return input;
    }

    protected boolean isRunning() {
        return running;
    }

    protected void setRunning(boolean value) {
        this.running = value;
    }

    private String configString() {
        return "Client{" +
                "serverHost='" + serverHost + '\'' +
                ", serverPort=" + serverPort +
                '}';
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}
