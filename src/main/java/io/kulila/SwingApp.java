package io.kulila;

import com.fasterxml.jackson.databind.JsonNode;
import io.kulila.client.ClientFX;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

public class SwingApp extends JFrame {

    private final ClientFX client = new ClientFX();
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Creation Date"}, 0);
    private final JTable projectTable = new JTable(tableModel);
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JTextField projectNameField = new JTextField();

    public SwingApp() {
        setTitle("SwingApp - Auth + Project + Client");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        client.start();

        JPanel authPanel = new JPanel(new GridLayout(3, 2));
        authPanel.setBorder(BorderFactory.createTitledBorder("Authentication"));
        authPanel.add(new JLabel("Username:"));
        authPanel.add(usernameField);
        authPanel.add(new JLabel("Password:"));
        authPanel.add(passwordField);

        JButton loginBtn = new JButton("Login");
        JButton signupBtn = new JButton("Signup");

        authPanel.add(loginBtn);
        authPanel.add(signupBtn);

        loginBtn.addActionListener(e -> handleLogin());
        signupBtn.addActionListener(e -> handleSignup());

        JPanel projectPanel = new JPanel(new BorderLayout());
        projectPanel.setBorder(BorderFactory.createTitledBorder("Projects"));

        JScrollPane tableScroll = new JScrollPane(projectTable);
        projectPanel.add(tableScroll, BorderLayout.CENTER);

        JPanel crudPanel = new JPanel(new GridLayout(1, 4));
        projectNameField.setToolTipText("Project Name");
        JButton createBtn = new JButton("Create");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");

        crudPanel.add(projectNameField);
        crudPanel.add(createBtn);
        crudPanel.add(updateBtn);
        crudPanel.add(deleteBtn);

        createBtn.addActionListener(e -> createProject());
        updateBtn.addActionListener(e -> updateProject());
        deleteBtn.addActionListener(e -> deleteProject());

        projectPanel.add(crudPanel, BorderLayout.SOUTH);

        add(authPanel, BorderLayout.NORTH);
        add(projectPanel, BorderLayout.CENTER);
    }

    private void handleSignup() {
        String user = usernameField.getText();
        String pass = new String(passwordField.getPassword());
        JsonNode res = client.signup(user, pass);
        JOptionPane.showMessageDialog(this, res.toPrettyString());
    }

    private void handleLogin() {
        String user = usernameField.getText();
        String pass = new String(passwordField.getPassword());
        JsonNode res = client.login(user, pass);
        JOptionPane.showMessageDialog(this, res.toPrettyString());
        if ("SUCCESS".equals(res.path("status").asText())) loadProjects();
    }

    private void loadProjects() {
        tableModel.setRowCount(0);
        JsonNode res = client.getProjects();
        JsonNode data = res.get("data");
        if (data != null && data.isArray()) {
            for (JsonNode proj : data) {
                Vector<Object> row = new Vector<>();
                row.add(proj.get("id").asInt());
                row.add(proj.get("name").asText());
                row.add(proj.get("creation_date").asText());
                tableModel.addRow(row);
            }
        }
    }

    private void createProject() {
        String name = projectNameField.getText();
        if (name.isBlank()) return;
        JsonNode res = client.createProject(name);
        JOptionPane.showMessageDialog(this, res.toPrettyString());
        loadProjects();
    }

    private void updateProject() {
        int selectedRow = projectTable.getSelectedRow();
        if (selectedRow == -1) return;
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String name = projectNameField.getText();
        if (name.isBlank()) return;
        JsonNode res = client.updateProject(id, name);
        JOptionPane.showMessageDialog(this, res.toPrettyString());
        loadProjects();
    }

    private void deleteProject() {
        int selectedRow = projectTable.getSelectedRow();
        if (selectedRow == -1) return;
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        JsonNode res = client.deleteProject(id);
        JOptionPane.showMessageDialog(this, res.toPrettyString());
        loadProjects();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SwingApp().setVisible(true);
        });
    }
}
