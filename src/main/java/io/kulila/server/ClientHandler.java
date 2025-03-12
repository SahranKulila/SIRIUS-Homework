package io.kulila.server;

import io.kulila.database.QueryExecutor;
import io.kulila.database.ConnectionPool;

import io.kulila.dataclass.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

public class ClientHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;
    private BufferedReader input;
    private PrintWriter output;
    private boolean running = false;
    private Connection connection;
    private final ConnectionPool connectionPool;
    private final QueryExecutor queryExecutor;

    public ClientHandler(Socket clientSocket, ConnectionPool connectionPool, QueryExecutor queryExecutor) {
        this.clientSocket = clientSocket;
        this.connectionPool = connectionPool;
        this.queryExecutor = queryExecutor;
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
                } else if ("JSON_FILE".equalsIgnoreCase(clientMessage)) {
                    JsonFileHandler jsonFileHandler = new JsonFileHandler();
                    String result = jsonFileHandler.sendJsonFile(output);
                    output.println(result);
                } else if ("JSON_OBJECT".equalsIgnoreCase(clientMessage)) {
                    Project project = new Project();
                    project.setProjectId("proj123");
                    project.setUserId("user456");
                    project.setName("Sample Project");
                    project.setDescription("This is a sample project.");
                    project.setCreatedAt(new Date(2023, 10, 01));
                    project.setUpdatedAt(new Date(2023, 10, 02));
                    project.setSceneData("{}");
                    project.setMetadata("{}");
                    project.setAssetId("asset123");
                    project.setConfigId("config456");
                    project.setExtensionId("ext789");

                    JsonObjectHandler jsonObjectHandler = new JsonObjectHandler();
                    String result = jsonObjectHandler.sendJsonObject(output, project);
                    output.println(result);    
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

    private void sendUsageInstructions() {
        String instructions = "Welcome to the server! Available commands:\n" +
                "1. CONNECT - Connect to the database.\n" +
                "2. QUERY <SQL> - Execute a SQL query (Don't forget ';')).\n" +
                "3. JSON_FILE - Send a JSON file to the server (Still working on it).\n" +
                "4. JSON_OBJECT - Send a JSON object to the server (Only hardcoded for now sorry).\n" +
                "5. exit - Disconnect from the server.";
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
