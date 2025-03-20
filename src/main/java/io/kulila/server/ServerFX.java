package io.kulila.server;

import io.kulila.utils.YamlConfigurator;
import io.kulila.database.ConnectionPool;
import io.kulila.database.QueryExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerFX {
    private static final Logger logger = LoggerFactory.getLogger(ServerFX.class);

    private int port = 8080;
    private int maxClients = 10;
    private int maxPoolSize = 5;
    private long connectionTimeoutMillis = 5000;
    private long queryTimeoutMillis = 10000;

    private boolean running = false;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private final ConnectionPool connectionPool;
    private final QueryExecutor queryExecutor;
    private final Map<Class<?>, List<Object>> storedObjects;

    public ServerFX() {
        this("server-config.yaml");
    }

    public ServerFX(String yamlFile) {
        loadConfig(yamlFile);

        this.connectionPool = new ConnectionPool(maxPoolSize, connectionTimeoutMillis);
        this.queryExecutor = new QueryExecutor(maxClients, queryTimeoutMillis);
        this.threadPool = Executors.newFixedThreadPool(maxClients);
        this.storedObjects = new java.util.HashMap<>();

        logger.info("ServerFX initialized.");
    }

    private void loadConfig(String yamlFile) {
        try {
            YamlConfigurator.configure(this, yamlFile);
            logger.info("Loaded ServerFX configuration from {}", yamlFile);
        } catch (Exception e) {
            logger.error("Failed to load configuration from {}. Using defaults.", yamlFile, e);
        }
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            logger.info("ServerFX started on port {}", port);
            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

            while (running) {
                Socket clientSocket = serverSocket.accept();
                logger.info("New client connected: {}", clientSocket.getInetAddress());

                threadPool.execute(new ClientHandlerFX(clientSocket, connectionPool, queryExecutor, storedObjects));
            }
        } catch (IOException e) {
            logger.error("Error starting ServerFX: {}", e.getMessage());
        } finally {
            stop();
        }
    }

    public void stop() {
        if (!running) return;
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            threadPool.shutdown();
            connectionPool.closeAllConnections();
            queryExecutor.shutdown();
            logger.info("ServerFX stopped.");
        } catch (IOException e) {
            logger.error("Error stopping ServerFX: {}", e.getMessage());
        }
    }

    public static void main(String[] args) {
        String yamlFile = args.length > 0 ? args[0] : "server-config.yaml";
        ServerFX serverFX = new ServerFX(yamlFile);
        serverFX.start();
    }
}
