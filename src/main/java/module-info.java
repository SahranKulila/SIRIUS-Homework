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
}