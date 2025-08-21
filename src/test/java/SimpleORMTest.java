package org.estivate;

import org.estivate.example.entity.User;
import org.estivate.db.DatabaseConnector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleORMTest {

    private final SimpleORM orm = new SimpleORM();

    @BeforeEach
    void setUp() throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            // Create table
            String createTableSql = "CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(255))";
            stmt.execute(createTableSql);

            // Insert test data
            String insertDataSql = "INSERT INTO users (id, name) VALUES (1, 'Test User')";
            stmt.execute(insertDataSql);
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            // Drop table
            String dropTableSql = "DROP TABLE users";
            stmt.execute(dropTableSql);
        }
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() throws Exception {
        User user = orm.findById(User.class, 1);

        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("Test User", user.getName());
    }

    @Test
    void update_shouldUpdateUser_whenUserExists() throws Exception {
        User user = orm.findById(User.class, 1);
        assertNotNull(user);

        user.setName("Updated User");
        orm.update(user);

        User updatedUser = orm.findById(User.class, 1);
        assertNotNull(updatedUser);
        assertEquals("Updated User", updatedUser.getName());
    }

    @Test
    void delete_shouldDeleteUser_whenUserExists() throws Exception {
        User user = orm.findById(User.class, 1);
        assertNotNull(user);

        orm.delete(user);

        User deletedUser = orm.findById(User.class, 1);
        assertNull(deletedUser);
    }
}
