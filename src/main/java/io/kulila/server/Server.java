package io.kulila.server;

import io.kulila.database.ConnectionPool;
import io.kulila.database.QueryExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private int port;
    private boolean running = false;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
//    private int maxClients;

    private final ConnectionPool connectionPool;
    private final QueryExecutor queryExecutor;

    public Server(int port, int maxClients, ConnectionPool connectionPool, QueryExecutor queryExecutor) {
        this.port = port;
        this.connectionPool = connectionPool;
        this.queryExecutor = queryExecutor;
        this.threadPool = Executors.newFixedThreadPool(maxClients);
    }

//    public Server(int port, int maxClients) {
//        this.port = port;
//        this.maxClients = maxClients;
//        this.threadPool = Executors.newFixedThreadPool(maxClients);
//    }

//    public Server() {
//        this(8080, 10); // Default port 8080, max 10 clients
//    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            logger.info("Server started on port {}", port);
            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

            while (running) {
                Socket clientSocket = serverSocket.accept();
                logger.info("New client connected: {}", clientSocket.getInetAddress());
                threadPool.execute(new ClientHandler(clientSocket, connectionPool, queryExecutor));
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
}
