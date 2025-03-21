module io.kulila.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires org.slf4j;
    requires com.fasterxml.jackson.databind;
    requires org.yaml.snakeyaml;
    requires java.sql;

    exports io.kulila.dataclass to com.fasterxml.jackson.databind;

    opens io.kulila.gui to javafx.fxml;
    exports io.kulila.gui;
    exports io.kulila;

    requires javafx.graphics;

    opens io.kulila.gui.controllers to javafx.fxml;
    exports io.kulila.gui.controllers;

    opens io.kulila.gui.managers to javafx.fxml;
    exports io.kulila.gui.managers;

    opens io.kulila.gui.models to javafx.fxml;
    exports io.kulila.gui.models;

    opens io.kulila.gui.models.shapes to javafx.fxml;
    exports io.kulila.gui.models.shapes;

    opens io.kulila.gui.rendering to javafx.fxml;
    exports io.kulila.gui.rendering;

    opens io.kulila.gui.utils to javafx.fxml;
    exports io.kulila.gui.utils;

    opens io.kulila.gui.config to javafx.fxml;
    exports io.kulila.gui.config;

    exports io.kulila.client;
    opens io.kulila.client to javafx.fxml, com.fasterxml.jackson.databind;

    exports io.kulila.database;
    opens io.kulila.database to javafx.fxml;

    exports io.kulila.server;
    opens io.kulila.server to javafx.fxml;

    exports io.kulila.utils;
    opens io.kulila.utils to javafx.fxml;
}