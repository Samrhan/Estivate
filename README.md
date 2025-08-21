# Estivate ORM

Estivate ORM is a simple, lightweight Object-Relational Mapping (ORM) framework for Java. It provides basic database operations for mapping Java objects to database tables.

## Features

-   **CRUD Operations:** Supports Create (`insert`), Read (`findById`), Update (`update`), and Delete (`delete`) operations.
-   **Annotation-based Mapping:** Uses annotations to map Java objects to database tables and fields:
    -   `@Table`: Specifies the table name for an entity.
    -   `@Column`: Specifies the column name for a field.
    -   `@Id`: Marks a field as the primary key.
-   **External Configuration:** Database connection details are externalized in a `database.properties` file.

## Build Instructions

This project is built with Apache Maven. To build the project, run the following command from the root directory:

```bash
mvn clean install
```

This will compile the source code, run tests, and package the application into a JAR file in the `target/` directory.

## Usage Example

Here is a simple example of how to use Estivate ORM with a `User` entity.

### 1. Define your Entity

Create a simple Java object (POJO) and annotate it with `@Table`, `@Column`, and `@Id`.

```java
package org.estivate.example.entity;

import org.estivate.annotations.Column;
import org.estivate.annotations.Id;
import org.estivate.annotations.Table;

@Table(name = "users")
public class User {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    // Getters and setters...
}
```

### 2. Use the ORM

You can use the `SimpleORM` class to perform database operations on your entities.

```java
import org.estivate.SimpleORM;
import org.estivate.example.entity.User;

public class Main {
    public static void main(String[] args) throws Exception {
        SimpleORM orm = new SimpleORM();

        // Create a new user
        User newUser = new User();
        newUser.setId(1);
        newUser.setName("John Doe");
        orm.insert(newUser);
        System.out.println("User inserted.");

        // Find the user
        User foundUser = orm.findById(User.class, 1);
        System.out.println("Found user: " + foundUser.getName());

        // Update the user
        foundUser.setName("John Doe Updated");
        orm.update(foundUser);
        System.out.println("User updated.");

        // Delete the user
        orm.delete(foundUser);
        System.out.println("User deleted.");
    }
}
```

## Running the Demonstration

The project includes a `Main.java` class that demonstrates the full functionality of the ORM. To run it, you first need to have a PostgreSQL database server running.

1.  **Create a database** named `estivate`.
2.  **Configure the connection** in `src/main/resources/database.properties`.
3.  **Run the `Main` class.** The application will automatically create the `users` table and run the demonstration.

You can run the `Main` class from your IDE or by executing the following command from the project root:

```bash
mvn exec:java -Dexec.mainClass="org.estivate.example.Main"
```
