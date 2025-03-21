package io.kulila;

import com.fasterxml.jackson.databind.JsonNode;
import io.kulila.client.ClientFX;
import io.kulila.gui.Main3DApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

public class SwingApp extends JFrame {

    private static final Logger logger = LoggerFactory.getLogger(SwingApp.class);

    private final ClientFX client = new ClientFX();
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Creation Date"}, 0);
    private final JTable projectTable = new JTable(tableModel);

    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JTextField projectNameField = new JTextField();

    private final JButton loginBtn = new JButton("Login");
    private final JButton signupBtn = new JButton("Signup");
    private final JButton createBtn = new JButton("Create");
    private final JButton updateBtn = new JButton("Update");
    private final JButton deleteBtn = new JButton("Delete");
    private final JButton refreshBtn = new JButton("Refresh");

    private final JButton launch3DButton = new JButton("Launch 3D View");

    public SwingApp() {
        setTitle("SwingApp - Auth & Project Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        client.start();
        setupAuthPanel();
        setupProjectPanel();
        disableProjectButtons();
    }

    private void setupAuthPanel() {
        JPanel authPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        authPanel.setBorder(BorderFactory.createTitledBorder("Authentication"));
        authPanel.add(new JLabel("Username:"));
        authPanel.add(usernameField);
        authPanel.add(new JLabel("Password:"));
        authPanel.add(passwordField);
        authPanel.add(loginBtn);
        authPanel.add(signupBtn);
        add(authPanel, BorderLayout.NORTH);

        loginBtn.addActionListener(e -> handleLogin());
        signupBtn.addActionListener(e -> handleSignup());
    }

    private void setupProjectPanel() {
        JPanel projectPanel = new JPanel(new BorderLayout());
        projectPanel.setBorder(BorderFactory.createTitledBorder("Projects"));

        JScrollPane tableScroll = new JScrollPane(projectTable);
        projectPanel.add(tableScroll, BorderLayout.CENTER);

        JPanel crudPanel = new JPanel(new GridLayout(1, 5, 5, 5));
        projectNameField.setToolTipText("Project Name");

        crudPanel.add(projectNameField);
        crudPanel.add(createBtn);
        crudPanel.add(updateBtn);
        crudPanel.add(deleteBtn);
        crudPanel.add(refreshBtn);
        crudPanel.add(launch3DButton);

        projectPanel.add(crudPanel, BorderLayout.SOUTH);
        add(projectPanel, BorderLayout.CENTER);

        createBtn.addActionListener(e -> createProject());
        updateBtn.addActionListener(e -> updateProject());
        deleteBtn.addActionListener(e -> deleteProject());
        refreshBtn.addActionListener(e -> loadProjects());
        launch3DButton.addActionListener(e -> launch3DApp());
    }

    private void launch3DApp() {
        try {
            logger.info("Launching Main3DApplication...");
            new Thread(() -> Main3DApplication.main(new String[0])).start();
        } catch (Exception e) {
            logger.error("Failed to launch Main3DApplication", e);
            JOptionPane.showMessageDialog(this, "Failed to launch 3D view: " + e.getMessage());
        }
    }

    private void handleSignup() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password must not be empty.");
            return;
        }

        JsonNode res = client.signup(user, pass);
        logger.info("Signup response: {}", res.toPrettyString());
        JOptionPane.showMessageDialog(this, res.toPrettyString());
        clearAuthFields();
    }

    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password must not be empty.");
            return;
        }

        JsonNode res = client.login(user, pass);
        logger.info("Login response: {}", res.toPrettyString());
        JOptionPane.showMessageDialog(this, res.toPrettyString());

        if ("SUCCESS".equals(res.path("status").asText())) {
            enableProjectButtons();
            loadProjects();
        }

        clearAuthFields();
    }

    private void loadProjects() {
        tableModel.setRowCount(0);
        JsonNode res = client.getProjects();
        logger.info("GET_PROJECTS response: {}", res.toPrettyString());

        if (!res.has("status") || !"SUCCESS".equals(res.get("status").asText())) {
            JOptionPane.showMessageDialog(this, res.toPrettyString());
            return;
        }

        JsonNode data = res.get("data");
        if (data == null || !data.isArray()) {
            logger.warn("GET_PROJECTS returned no array under 'data'");
            JOptionPane.showMessageDialog(this, "No projects found.");
            return;
        }

        for (JsonNode proj : data) {
            JsonNode idNode = proj.get("id");
            JsonNode nameNode = proj.get("name");
            JsonNode dateNode = proj.get("creationDate");

            if (idNode == null || nameNode == null || dateNode == null) {
                logger.warn("Skipping project with missing fields: {}", proj.toPrettyString());
                continue;
            }

            Vector<Object> row = new Vector<>();
            row.add(idNode.asInt());
            row.add(nameNode.asText());
            row.add(dateNode.asText());
            tableModel.addRow(row);
        }
    }

    private void createProject() {
        String name = projectNameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Project name must not be empty.");
            return;
        }

        JsonNode res = client.createProject(name);
        logger.info("CREATE_PROJECT response: {}", res.toPrettyString());
        JOptionPane.showMessageDialog(this, res.toPrettyString());
        projectNameField.setText("");
        loadProjects();
    }

    private void updateProject() {
        int selectedRow = projectTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a project to update.");
            return;
        }

        String name = projectNameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Project name must not be empty.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        JsonNode res = client.updateProject(id, name);
        logger.info("UPDATE_PROJECT response: {}", res.toPrettyString());
        JOptionPane.showMessageDialog(this, res.toPrettyString());
        projectNameField.setText("");
        loadProjects();
    }

    private void deleteProject() {
        int selectedRow = projectTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a project to delete.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete project ID " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        JsonNode res = client.deleteProject(id);
        logger.info("DELETE_PROJECT response: {}", res.toPrettyString());
        JOptionPane.showMessageDialog(this, res.toPrettyString());
        loadProjects();
    }

    private void enableProjectButtons() {
        createBtn.setEnabled(true);
        updateBtn.setEnabled(true);
        deleteBtn.setEnabled(true);
        refreshBtn.setEnabled(true);
    }

    private void disableProjectButtons() {
        createBtn.setEnabled(false);
        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        refreshBtn.setEnabled(false);
    }

    private void clearAuthFields() {
        usernameField.setText("");
        passwordField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SwingApp().setVisible(true));
    }
}
