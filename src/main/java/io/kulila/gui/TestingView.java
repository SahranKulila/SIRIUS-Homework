package io.kulila.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TestingView extends Application {

    private Object currentObject;
    private List<TextField> inputFields = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Dynamic Object Editor");

        Person person = new Person("John Appleseed", 30, 170);
        setObjectToEdit(person);

        VBox vbox = new VBox(10);
        vbox.getChildren().add(new Label("Object Editor"));

        Label objectNameLabel = new Label("Object: " + currentObject.getClass().getSimpleName());
        vbox.getChildren().add(objectNameLabel);

        Field[] fields = currentObject.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Label fieldLabel = new Label(fields[i].getName() + ":");
            vbox.getChildren().add(fieldLabel);
            vbox.getChildren().add(inputFields.get(i));
        }

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> saveChanges());
        vbox.getChildren().add(saveButton);

        Scene scene = new Scene(vbox, 300, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setObjectToEdit(Object object) {
        this.currentObject = object;
        inputFields.clear();

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                TextField textField = new TextField();
                textField.setText(String.valueOf(field.get(object)));
                inputFields.add(textField);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveChanges() {
        Field[] fields = currentObject.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            try {
                String value = inputFields.get(i).getText();
                Class<?> fieldType = fields[i].getType();
                if (fieldType == int.class) {
                    fields[i].setInt(currentObject, Integer.parseInt(value));
                } else if (fieldType == double.class) {
                    fields[i].setDouble(currentObject, Double.parseDouble(value));
                } else if (fieldType == boolean.class) {
                    fields[i].setBoolean(currentObject, Boolean.parseBoolean(value));
                } else {
                    fields[i].set(currentObject, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Object updated: " + currentObject);
    }

    public static class Person {
        private String name;
        private int age;
        private int height;

        public Person(String name, int age, int height) {
            this.name = name;
            this.age = age;
            this.height = height;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", height=" + height +
                    '}';
        }
    }
}
