package org.estivate;

import org.estivate.db.DatabaseConnector;
import org.estivate.example.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

// Note: These tests are disabled by default as they require a running PostgreSQL database.
// To run them, you need to:
// 1. Have a PostgreSQL instance running.
// 2. Create a database named 'estivate'.
// 3. Configure the connection details in 'src/main/resources/database.properties'.
// 4. Remove or comment out the @Disabled annotation below.
@Disabled("Requires a running PostgreSQL database")
public class SimpleORMIntegrationTest {

    private final SimpleORM orm = new SimpleORM();

    @BeforeEach
    void setUp() throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            // Create table
            String createTableSql = "CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(255))";
            stmt.execute(createTableSql);
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
    void insertAndFindById_shouldPersistAndRetrieveUser() throws Exception {
        User newUser = new User();
        newUser.setId(1);
        newUser.setName("Integration Test User");
        orm.insert(newUser);

        User foundUser = orm.findById(User.class, 1);
        assertNotNull(foundUser);
        assertEquals(1, foundUser.getId());
        assertEquals("Integration Test User", foundUser.getName());
    }

    @Test
    void update_shouldModifyExistingUser() throws Exception {
        User user = new User();
        user.setId(2);
        user.setName("To be updated");
        orm.insert(user);

        user.setName("Updated Name");
        orm.update(user);

        User updatedUser = orm.findById(User.class, 2);
        assertNotNull(updatedUser);
        assertEquals("Updated Name", updatedUser.getName());
    }

    @Test
    void delete_shouldRemoveExistingUser() throws Exception {
        User user = new User();
        user.setId(3);
        user.setName("To be deleted");
        orm.insert(user);

        orm.delete(user);

        User deletedUser = orm.findById(User.class, 3);
        assertNull(deletedUser);
    }
}
