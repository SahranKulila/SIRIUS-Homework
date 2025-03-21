package io.kulila.database;

import io.kulila.dataclass.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {
    private static final Logger logger = LoggerFactory.getLogger(ProjectDAO.class);

    private final Connection connection;

    public ProjectDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                projects.add(new Project(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("creation_date")
                ));
            }
        } catch (SQLException e) {
            logger.error("Error fetching projects: {}", e.getMessage());
        }
        return projects;
    }

    public Project createProject(String name) {
        String sql = "INSERT INTO projects (name, creation_date) VALUES (?, NOW())";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return new Project(
                                generatedKeys.getInt(1),
                                name,
                                new Timestamp(System.currentTimeMillis()).toString()
                        );
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating project: {}", e.getMessage());
        }
        return null;
    }

    public boolean updateProject(Project project) {
        String sql = "UPDATE projects SET name = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, project.getName());
            statement.setInt(2, project.getId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating project: {}", e.getMessage());
            return false;
        }
    }

    public boolean deleteProject(int projectId) {
        String sql = "DELETE FROM projects WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, projectId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting project: {}", e.getMessage());
            return false;
        }
    }
}
