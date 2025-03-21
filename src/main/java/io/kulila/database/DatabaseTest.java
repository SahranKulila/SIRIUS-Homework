package io.kulila.test;

import io.kulila.database.*;
import io.kulila.dataclass.User;
import io.kulila.dataclass.Project;

import java.sql.Connection;
import java.util.List;

public class DatabaseTest {
    public static void main(String[] args) {
        // Initialize connection pool
        ConnectionPool connectionPool = new ConnectionPool(5, 3000);

        try {
            // Get connection from pool
            Connection connection = connectionPool.getConnection();

            // === Test UserDAO ===
            UserDAO userDAO = new UserDAO(connection);
            User testUser = new User("testuser123", "testpass");

            System.out.println("Inserting user...");
            boolean inserted = userDAO.insertUser(testUser);
            System.out.println("User inserted: " + inserted);

            System.out.println("Validating user...");
            boolean valid = userDAO.validateUser(testUser.getUsername(), testUser.getPassword());
            System.out.println("User valid: " + valid);

            // === Test ProjectDAO ===
            ProjectDAO projectDAO = new ProjectDAO(connection);

            System.out.println("Creating project...");
            Project newProject = projectDAO.createProject("Test Project");
            if (newProject != null) {
                System.out.println("Created project: " + newProject.getName());

                System.out.println("Updating project...");
                newProject.setName("Updated Project Name");
                boolean updated = projectDAO.updateProject(newProject);
                System.out.println("Project updated: " + updated);

                System.out.println("Fetching all projects...");
                List<Project> projects = projectDAO.getAllProjects();
                projects.forEach(p -> System.out.println(p.getId() + ": " + p.getName()));

                System.out.println("Deleting project...");
                boolean deleted = projectDAO.deleteProject(newProject.getId());
                System.out.println("Project deleted: " + deleted);
            }

            // === Test QueryExecutor ===
            QueryExecutor executor = new QueryExecutor(2, 2000);

            System.out.println("Executing raw SELECT query...");
            String selectResult = executor.executeQuery(connection, "SELECT * FROM users");
            System.out.println(selectResult);

            System.out.println("Executing raw UPDATE query...");
            String updateResult = executor.executeQuery(connection, "UPDATE users SET password='newpass' WHERE username='testuser123'");
            System.out.println(updateResult);

            executor.shutdown();

            // Release connection back to the pool
            connectionPool.releaseConnection(connection);
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close all pooled connections
            connectionPool.closeAllConnections();
        }
    }
}
