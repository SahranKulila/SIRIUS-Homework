package io.kulila.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private final String serverHost;
    private final int serverPort;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private Scanner scanner;
    private boolean running = false;

    public Client(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public Client() {
        this("localhost", 8080);
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

    public static void main(String[] args) {
        String host = "localhost"; // Default host
        int port = 8080; // Default port

        if (args.length >= 2) {
            try {
                host = args[0];
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                logger.warn("Invalid arguments. Using default values: host={}, port={}",
                        host, port);
            }
        }

        Client client = new Client(host, port);
        client.start();
    }
}
