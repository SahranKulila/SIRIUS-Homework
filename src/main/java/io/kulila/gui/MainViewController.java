package io.kulila.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import io.kulila.client.InlineClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    private final InlineClient client = new InlineClient();
    private final ObservableList<Project> projects = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        client.connect();
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
        String response = client.sendMessage("SELECT * FROM projects");
        projects.clear();
        if (response != null && !response.equals("null")) {
            String[] rows = response.split(";");
            for (String row : rows) {
                String[] parts = row.split(",");
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                String date = parts[2];
                projects.add(new Project(id, name, date));
            }
        }
    }

    private void createProject() {
        client.sendMessage("INSERT INTO projects (user_id, name) VALUES (1, 'New Project')");
        loadProjects();
    }

    private void updateProject() {
        Project selected = projectTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            client.sendMessage("UPDATE projects SET name = 'Updated Project' WHERE id = " + selected.id());
            loadProjects();
        }
    }

    private void deleteProject() {
        Project selected = projectTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            client.sendMessage("DELETE FROM projects WHERE id = " + selected.id());
            loadProjects();
        }
    }
}

record Project(int id, String name, String date) {
}
