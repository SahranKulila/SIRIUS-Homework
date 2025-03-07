package io.kulila.server;

import io.kulila.database.ConnectionPool;
import io.kulila.database.QueryExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {
    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {
        int port = 8080; // Default port
        int maxClients = 10; // Default max clients
        int maxPoolSize = 5; // Default connection pool size
        long connectionTimeoutMillis = 5000; // Default connection timeout
        long queryTimeoutMillis = 10000; // Default query timeout

        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[0]);
                maxClients = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                logger.warn("Invalid arguments. Using default values: port={}, maxClients={}", port, maxClients);
            }
        }

        ConnectionPool connectionPool = new ConnectionPool(maxPoolSize, connectionTimeoutMillis);
        QueryExecutor queryExecutor = new QueryExecutor(maxClients, queryTimeoutMillis);

        Server server = new Server(port, maxClients, connectionPool, queryExecutor);

        logger.info("Starting server on port {} with max {} clients", port, maxClients);
        server.start();
    }
}
