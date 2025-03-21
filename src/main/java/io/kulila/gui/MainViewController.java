package io.kulila.gui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import io.kulila.client.ClientFX;
import io.kulila.dataclass.Project;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainViewController {
    private static final Logger logger = LoggerFactory.getLogger(MainViewController.class);
    private final ObservableList<Project> projectList = FXCollections.observableArrayList();
    private ClientFX clientFX;

    @FXML
    private TableView<Project> projectTable;
    @FXML
    private TableColumn<Project, Integer> colId;
    @FXML
    private TableColumn<Project, String> colName;
    @FXML
    private TableColumn<Project, String> colDate;
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnLogout;

    public void setClientFX(ClientFX clientFX) {
        this.clientFX = clientFX;
    }

    @FXML
    private void initialize() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colDate.setCellValueFactory(cellData -> cellData.getValue().creationDateProperty());

        loadProjects();

        btnCreate.setOnAction(event -> handleCreate());
        btnUpdate.setOnAction(event -> handleUpdate());
        btnDelete.setOnAction(event -> handleDelete());
        btnLogout.setOnAction(event -> handleLogout());
    }

    private void loadProjects() {
        if (clientFX == null) return;
        clientFX.sendJsonRequestFX("GET_PROJECTS", null, responseNode -> {
            if ("SUCCESS".equals(responseNode.get("status").asText())) {
                List<Project> projects = clientFX.getObjectMapper().convertValue(responseNode.get("data"), new TypeReference<>() {});
                Platform.runLater(() -> projectList.setAll(projects));
                projectTable.setItems(projectList);
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to load projects.");
            }
        });
    }

    private void handleCreate() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Project");
        dialog.setHeaderText("Enter project name:");
        dialog.setContentText("Name:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty() && clientFX != null) {
                Map<String, Object> requestData = new HashMap<>();
                requestData.put("name", name);

                clientFX.sendJsonRequestFX("CREATE_PROJECT", requestData, responseNode -> {
                    if ("SUCCESS".equals(responseNode.get("status").asText())) {
                        Project newProject = clientFX.getObjectMapper().convertValue(responseNode.get("data"), Project.class);
                        Platform.runLater(() -> projectList.add(newProject));
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Project creation failed.");
                    }
                });
            }
        });
    }

    private void handleUpdate() {
        Project selected = projectTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a project to update.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.getName());
        dialog.setTitle("Update Project");
        dialog.setHeaderText("Edit project name:");
        dialog.setContentText("New name:");

        dialog.showAndWait().ifPresent(newName -> {
            if (!newName.trim().isEmpty()) {
                selected.setName(newName);
                clientFX.sendJsonRequestFX("UPDATE_PROJECT", selected, responseNode -> {
                    if ("SUCCESS".equals(responseNode.get("status").asText())) {
                        Platform.runLater(projectTable::refresh);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Project update failed.");
                    }
                });
            }
        });
    }

    private void handleDelete() {
        Project selected = projectTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a project to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this project?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                Map<String, Object> requestData = new HashMap<>();
                requestData.put("id", selected.getId());

                clientFX.sendJsonRequestFX("DELETE_PROJECT", requestData, responseNode -> {
                    if ("SUCCESS".equals(responseNode.get("status").asText())) {
                        Platform.runLater(() -> projectList.remove(selected));
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Project deletion failed.");
                    }
                });
            }
        });
    }

    private void handleLogout() {
        SceneLoader.loadScene((Stage) btnLogout.getScene().getWindow(), "/views.fxml/LoginView.fxml", clientFX);
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setContentText(message);
            alert.show();
        });
    }
}
