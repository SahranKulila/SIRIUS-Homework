package io.kulila.server;

import io.kulila.database.QueryExecutor;
import io.kulila.database.ConnectionPool;

import io.kulila.dataclass.Project;
import io.kulila.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;
    private BufferedReader input;
    protected PrintWriter output;
    private boolean running = false;
    private Connection connection;
    private final ConnectionPool connectionPool;
    private final QueryExecutor queryExecutor;
    private final Map<Class<?>, List<Object>> storedObjects;

    public ClientHandler(Socket clientSocket,
                         ConnectionPool connectionPool,
                         QueryExecutor queryExecutor,
                         Map<Class<?>, List<Object>> storedObjects) {
        this.clientSocket = clientSocket;
        this.connectionPool = connectionPool;
        this.queryExecutor = queryExecutor;
        this.storedObjects = storedObjects;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);

            logger.info("Client handler started for {}", clientSocket.getInetAddress());
            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

            sendUsageInstructions();

            String clientMessage;
            while (running && (clientMessage = input.readLine()) != null) {
                logger.info("Received: {}", clientMessage);

                if ("CONNECT".equalsIgnoreCase(clientMessage)) {
                    connection = connectionPool.getConnection();
                    output.println("Connected to database.");
                } else if ("QUERY".equalsIgnoreCase(clientMessage.split(" ")[0])) {
                    if (connection == null) {
                        output.println("Error: Not connected to database. Send 'CONNECT' first.");
                    } else {
                        String query = clientMessage.substring(5).trim();
                        String result = queryExecutor.executeQuery(connection, query);
                        output.println(result);
                    }
                } else if (clientMessage.startsWith("JSON_STRING ")) {
                    handleJsonString(clientMessage.substring(12).trim());
                } else if ("SHOW_OBJECTS".equalsIgnoreCase(clientMessage)) {
                    showStoredObjects();
                } else if ("exit".equalsIgnoreCase(clientMessage)) {
                    logger.info("Client requested disconnection.");
                    break;
                } else {
                    output.println("Server echo: " + clientMessage);
                }
            }
        } catch (IOException e) {
            logger.error("Error in client handler: {}", e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            stop();
        }
    }

    private void handleJsonString(String jsonString) {
        try {
            Object obj = JsonUtils.deserializeFromStringDynamic(jsonString);
            Class<?> objClass = obj.getClass();

            storedObjects.putIfAbsent(objClass, new ArrayList<>());
            storedObjects.get(objClass).add(obj);

            logger.info("Stored new object of type {}: {}", objClass.getSimpleName(), obj);
            output.println("Stored object: " + obj);
        } catch (Exception e) {
            logger.error("Failed to deserialize JSON: {}", e.getMessage());
            output.println("Error: Failed to deserialize JSON.");
        }
    }

    private void showStoredObjects() {
        if (storedObjects.isEmpty()) {
            output.println("No objects stored.");
            return;
        }

        StringBuilder response = new StringBuilder("Stored Objects:\n");
        for (List<Object> objectList : storedObjects.values()) {
            for (Object obj : objectList) {
                response.append(obj.toString()).append("\n");
            }
        }

        output.println(response.toString());
        logger.info("Displayed stored objects to client.");
    }

    private void sendUsageInstructions() {
        String instructions = "Welcome to the server! Available commands:\n" +
                "1. CONNECT - Connect to the database.\n" +
                "2. QUERY <SQL> - Execute a SQL query (Don't forget ';')).\n" +
                "3. JSON_STRING <json> - Send a JSON string to the server for processing.\n" +
                "4. exit - Disconnect from the server.";
        output.println(instructions);
    }

    public void stop() {
        if (!running) return;
        running = false;
        try {
            if (connection != null) {
                connectionPool.releaseConnection(connection);
            }
            if (input != null) input.close();
            if (output != null) output.close();
            if (clientSocket != null) clientSocket.close();
            assert clientSocket != null;
            logger.info("Client disconnected: {}", clientSocket.getInetAddress());
        } catch (IOException e) {
            logger.error("Error closing client connection: {}", e.getMessage());
        }
    }
}
