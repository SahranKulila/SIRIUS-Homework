package io.kulila.client;

import io.kulila.client.InlineClient;

public class InlineClientTest {
    public static void main(String[] args) {
        InlineClient client = new InlineClient();
        client.connect();

        String createTable = "CREATE TABLE IF NOT EXISTS test_users (id INT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(50), email VARCHAR(100));";
        System.out.println("Create Table: " + client.sendMessage(createTable));

        String insert1 = "INSERT INTO test_users (username, email) VALUES ('alice', 'alice@example.com');";
        String insert2 = "INSERT INTO test_users (username, email) VALUES ('bob', 'bob@example.com');";
        System.out.println("Insert 1: " + client.sendMessage(insert1));
        System.out.println("Insert 2: " + client.sendMessage(insert2));

        String selectAll = "SELECT * FROM test_users;";
        System.out.println("Select All: " + client.sendMessage(selectAll));

        String update = "UPDATE test_users SET email = 'bob@newdomain.com' WHERE username = 'bob';";
        System.out.println("Update: " + client.sendMessage(update));

        System.out.println("Select After Update: " + client.sendMessage(selectAll));

        String delete = "DELETE FROM test_users WHERE username = 'alice';";
        System.out.println("Delete: " + client.sendMessage(delete));

        System.out.println("Select After Delete: " + client.sendMessage(selectAll));

        String dropTable = "DROP TABLE test_users;";
        System.out.println("Drop Table: " + client.sendMessage(dropTable));

        client.disconnect();
    }
}
