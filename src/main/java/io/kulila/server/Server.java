package io.kulila.server;

import io.kulila.dataclass.Project;
import io.kulila.dataclass.TestObject;
import io.kulila.utils.JsonUtils;
import io.kulila.utils.YamlConfigurator;
import io.kulila.database.ConnectionPool;
import io.kulila.database.QueryExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private int port = 8080; // Default port
    private int maxClients = 10; // Default max clients
    private int maxPoolSize = 5; // Default connection pool size
    private long connectionTimeoutMillis = 5000; // Default connection timeout
    private long queryTimeoutMillis = 10000; // Default query timeout

    private boolean running = false;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private final ConnectionPool connectionPool;
    private final QueryExecutor queryExecutor;

    private static final Map<Class<?>, List<Object>> storedObjects = new HashMap<>();

    public Server() {
        this("server-config.yaml");
    }

    public Server(String yamlFile) {
        loadConfig(yamlFile);

        this.connectionPool = new ConnectionPool(maxPoolSize, connectionTimeoutMillis);
        this.queryExecutor = new QueryExecutor(maxClients, queryTimeoutMillis);
        this.threadPool = Executors.newFixedThreadPool(maxClients);

        JsonUtils.registerClass(Project.class);
        JsonUtils.registerClass(TestObject.class);
        logger.info("Registered classes for dynamic JSON deserialization");
    }

    private void loadConfig(String yamlFile) {
        try {
            YamlConfigurator.configure(this, yamlFile);
            logger.info("Loaded server configuration from {}", yamlFile);
        } catch (Exception e) {
            logger.error("Failed to load configuration from {}. Using defaults.", yamlFile, e);
        }
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            logger.info("Server started on port {}", port);
            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

            while (running) {
                Socket clientSocket = serverSocket.accept();
                logger.info("New client connected: {}", clientSocket.getInetAddress());
                threadPool.execute(new ClientHandler(clientSocket, connectionPool, queryExecutor, storedObjects));
            }
        } catch (IOException e) {
            logger.error("Error starting server: {}", e.getMessage());
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
            logger.info("Server stopped.");
        } catch (IOException e) {
            logger.error("Error stopping server: {}", e.getMessage());
        }
    }

    public static void main(String[] args) {
        String yamlFile = args.length > 0 ? args[0] : "server-config.yaml";
        Server server = new Server(yamlFile);
        server.start();
    }
}
