package io.kulila.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ConnectionPool {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);
    private final BlockingQueue<Connection> connectionPool;
    private final int maxPoolSize;
    private final long timeoutMillis;

    public ConnectionPool(int maxPoolSize, long timeoutMillis) {
        this.maxPoolSize = maxPoolSize;
        this.timeoutMillis = timeoutMillis;
        this.connectionPool = new LinkedBlockingQueue<>(maxPoolSize);
        initializePool();
    }

    private void initializePool() {
        for (int i = 0; i < maxPoolSize; i++) {
            try {
                Connection connection = DatabaseManager.getConnection();
                if (connection != null) {
                    if(!connectionPool.offer(connection)) {
                        logger.error("Pool size exceeded.");
                    }
                }
            } catch (Exception e) {
                logger.error("Error creating connection: {}", e.getMessage());
            }
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            Connection connection = connectionPool.poll(timeoutMillis, TimeUnit.MILLISECONDS);
            if (connection == null) {
                throw new SQLException("Timeout: Could not acquire a connection from the pool.");
            }
            return connection;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrupted while waiting for a connection.", e);
        }
    }

    public void releaseConnection(Connection connection) {
        if (connection != null) {
            if(!connectionPool.offer(connection)) {
                logger.warn("Failed to add connection to the pool: Pool is full.");
            }
        }
    }

    public void closeAllConnections() {
        for (Connection connection : connectionPool) {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.error("Error closing connection: {}", e.getMessage());
            }
        }
    }
}
