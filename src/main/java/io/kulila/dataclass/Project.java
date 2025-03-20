package io.kulila.dataclass;

import javafx.beans.property.*;

public class Project {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty creationDate;

    public Project(int id, String name, String creationDate) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.creationDate = new SimpleStringProperty(creationDate);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getCreationDate() {
        return creationDate.get();
    }

    public StringProperty creationDateProperty() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate.set(creationDate);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id.get() +
                ", name='" + name.get() + '\'' +
                ", creationDate='" + creationDate.get() + '\'' +
                '}';
    }
}
