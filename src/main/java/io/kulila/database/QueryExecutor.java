package io.kulila.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.concurrent.*;

public class QueryExecutor {
    private static final Logger logger = LoggerFactory.getLogger(QueryExecutor.class);
    private final ExecutorService executorService;
    private final long queryTimeoutMillis;

    public QueryExecutor(int threadPoolSize, long queryTimeoutMillis) {
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        this.queryTimeoutMillis = queryTimeoutMillis;
    }

    public String executeQuery(Connection connection, String query) {
        Future<String> future = executorService.submit(() -> {

            try (Statement statement = connection.createStatement()) {

                if (query.trim().toUpperCase().startsWith("SELECT")) {

                    ResultSet resultSet = statement.executeQuery(query);
                    StringBuilder result = new StringBuilder();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        result.append(metaData.getColumnName(i)).append("\t");
                    }
                    result.append("\n");

                    while (resultSet.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            result.append(resultSet.getString(i)).append("\t");
                        }
                        result.append("\n");
                    }

                    return result.toString();

                } else {

                    int rowsAffected = statement.executeUpdate(query);
                    return "Rows affected: " + rowsAffected;

                }
            } catch (SQLException e) {
                throw new RuntimeException("Query execution failed: " + e.getMessage(), e);
            }
        });

        try {

            return future.get(queryTimeoutMillis, TimeUnit.MILLISECONDS);

        } catch (TimeoutException e) {

            future.cancel(true);
            return "Error: Query execution timed out.";

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
