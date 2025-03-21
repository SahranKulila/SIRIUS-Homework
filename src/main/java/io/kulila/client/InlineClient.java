package io.kulila.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.kulila.utils.YamlConfigurator;

import java.io.*;
import java.net.Socket;

public class InlineClient {
    private static final Logger logger = LoggerFactory.getLogger(InlineClient.class);

    private String serverHost;
    private int serverPort;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private boolean running = false;

    public InlineClient() {
        this("client-config.yaml");
    }

    public InlineClient(String configPath) {
        loadConfig(configPath, "<<Inline Client>>");
    }

    private void loadConfig(String configPath, String debugInfo) {
        try {
            YamlConfigurator.configure(this, configPath);
            logger.info("Loaded config for {} {}", debugInfo, configString());
        } catch (Exception e) {
            logger.error("Failed to load configuration for {} from {}: {}", debugInfo, configPath, e.getMessage());
            this.serverHost = "localhost";
            this.serverPort = 8080;
        }
    }

    public void connect() {
        try {
            socket = new Socket(serverHost, serverPort);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            running = true;

            output.println("CONNECT");

            logger.info("Connected to server at {}:{}", serverHost, serverPort);
            Runtime.getRuntime().addShutdownHook(new Thread(this::disconnect));
        } catch (IOException e) {
            logger.error("Error connecting to server: {}", e.getMessage());
        }
    }

    public String sendMessage(String message) {
        if (!running || socket == null || socket.isClosed()) {
            logger.warn("Cannot send message. Client is not connected.");
            return null;
        }

        try {
            output.println("QUERY " + message + ";");
            return input.readLine();
        } catch (IOException e) {
            logger.error("Failed to send or receive message: {}", e.getMessage());
            return null;
        }
    }

    public void disconnect() {
        if (!running) return;
        running = false;
        try {
            if (socket != null) socket.close();
            if (input != null) input.close();
            if (output != null) output.close();
            logger.info("Client disconnected.");
        } catch (IOException e) {
            logger.error("Error while disconnecting: {}", e.getMessage());
        }
    }

    public boolean isRunning() {
        return running;
    }

    private String configString() {
        return "ProgrammaticClient{" +
                "serverHost='" + serverHost + '\'' +
                ", serverPort=" + serverPort +
                '}';
    }
}
