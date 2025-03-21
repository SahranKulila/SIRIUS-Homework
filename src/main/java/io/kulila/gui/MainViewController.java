package io.kulila.gui;

import com.fasterxml.jackson.databind.JsonNode;
import io.kulila.client.ClientFX;
import io.kulila.dataclass.Project;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainViewController {
    @FXML
    private Button btnLogout;
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;
    @FXML
    private TableView<Project> projectTable;
    @FXML
    private TableColumn<Project, Integer> colId;
    @FXML
    private TableColumn<Project, String> colName;
    @FXML
    private TableColumn<Project, String> colDate;

    private final ClientFX client = new ClientFX();
    private final ObservableList<Project> projects = FXCollections.observableArrayList();
    private static final Logger logger = LoggerFactory.getLogger(MainViewController.class);

    @FXML
    private void initialize() {
        client.start();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        projectTable.setItems(projects);
        loadProjects();

        btnLogout.setOnAction(event -> SceneLoader.loadScene((Stage) btnLogout.getScene().getWindow(), "/io/kulila/gui/LoginController.fxml"));
        btnCreate.setOnAction(event -> createProject());
        btnUpdate.setOnAction(event -> updateProject());
        btnDelete.setOnAction(event -> deleteProject());
    }

    private void loadProjects() {
        JsonNode response = client.getProjects();
        projects.clear();
        if ("SUCCESS".equals(response.get("status").asText())) {
            response.get("data").forEach(item -> {
                int id = item.get("id").asInt();
                String name = item.get("name").asText();
                String date = item.get("creationDate").asText();
                projects.add(new Project(id, name, date));
            });
        } else {
            logger.error("Failed to load projects: {}", response.get("message").asText());
        }
    }

    private void createProject() {
        JsonNode response = client.createProject("New Project");
        if ("SUCCESS".equals(response.get("status").asText())) {
            loadProjects();
        } else {
            logger.error("Failed to create project: {}", response.get("message").asText());
        }
    }

    private void updateProject() {
        Project selected = projectTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            JsonNode response = client.updateProject(selected.getId(), "Updated Project");
            if ("SUCCESS".equals(response.get("status").asText())) {
                loadProjects();
            } else {
                logger.error("Failed to update project: {}", response.get("message").asText());
            }
        }
    }

    private void deleteProject() {
        Project selected = projectTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            JsonNode response = client.deleteProject(selected.getId());
            if ("SUCCESS".equals(response.get("status").asText())) {
                loadProjects();
            } else {
                logger.error("Failed to delete project: {}", response.get("message").asText());
            }
        }
    }
}
