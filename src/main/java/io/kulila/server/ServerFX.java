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

public class ServerFX extends Server {
    private static final Logger logger = LoggerFactory.getLogger(ServerFX.class);
    private ExecutorService threadPool;

    public ServerFX() {
        super("server-config.yaml");
        this.threadPool = Executors.newFixedThreadPool(maxClients);
    }

    @Override
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            logger.info("ServerFX started on port {}", port);
            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

            while (running) {
                Socket clientSocket = serverSocket.accept();
                logger.info("New client connected: {}", clientSocket.getInetAddress());

                threadPool.execute(new ClientHandlerFX(
                        clientSocket,
                        connectionPool,
                        queryExecutor,
                        storedObjects
                ));
            }
        } catch (IOException e) {
            logger.error("Error starting ServerFX: {}", e.getMessage());
        } finally {
            stop();
        }
    }
}
