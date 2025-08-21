package org.estivate.example;

import org.estivate.SimpleORM;
import org.estivate.example.entity.User;
import org.estivate.db.DatabaseConnector;

import java.sql.Connection;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {
        System.out.println("Running SimpleORM demonstration...");

        // Note: This demonstration requires a running PostgreSQL database with the 'estivate' database created.
        // The connection details are configured in 'src/main/resources/database.properties'.
        // You also need a 'users' table. You can create it with the following SQL:
        // CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(255));

        try {
            // Setup: create the users table
            try (Connection conn = DatabaseConnector.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY, name VARCHAR(255))");
                // Clean up the table for a fresh run
                stmt.execute("DELETE FROM users");
            }

            SimpleORM orm = new SimpleORM();

            // 1. Insert a new user
            System.out.println("\n1. Inserting a new user...");
            User newUser = new User();
            newUser.setId(100);
            newUser.setName("John Doe");
            orm.insert(newUser);
            System.out.println("User inserted: " + newUser.getName());

            // 2. Find the user by ID
            System.out.println("\n2. Finding the user by ID...");
            User foundUser = orm.findById(User.class, 100);
            if (foundUser != null) {
                System.out.println("Found user: " + foundUser.getName() + " with ID: " + foundUser.getId());
            } else {
                System.out.println("User not found.");
            }

            // 3. Update the user's name
            System.out.println("\n3. Updating the user's name...");
            if (foundUser != null) {
                foundUser.setName("John Doe Updated");
                orm.update(foundUser);
                System.out.println("User updated.");
            }

            // 4. Find the user again to see the update
            System.out.println("\n4. Finding the user again...");
            User updatedUser = orm.findById(User.class, 100);
            if (updatedUser != null) {
                System.out.println("Found updated user: " + updatedUser.getName());
            } else {
                System.out.println("User not found.");
            }

            // 5. Delete the user
            System.out.println("\n5. Deleting the user...");
            if (updatedUser != null) {
                orm.delete(updatedUser);
                System.out.println("User deleted.");
            }

            // 6. Try to find the user again
            System.out.println("\n6. Verifying user deletion...");
            User deletedUser = orm.findById(User.class, 100);
            if (deletedUser == null) {
                System.out.println("User successfully deleted.");
            } else {
                System.out.println("User deletion failed.");
            }

        } catch (Exception e) {
            System.err.println("An error occurred during the demonstration:");
            e.printStackTrace();
        }
    }
}
