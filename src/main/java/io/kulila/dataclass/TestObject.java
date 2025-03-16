package io.kulila.dataclass;

public class TestObject {
    private String id;
    private String name;
    private int value;

    public TestObject() {
    }

    public TestObject(String id, String name, int value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TestObject{id='" + id + "', name='" + name + "', value=" + value + "}";
    }
}
