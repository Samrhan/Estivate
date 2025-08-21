# Project TODO List

## 1. Refactor Database Connection
- [x] Remove the duplicate and unused `DatabaseConnector` interface.
- [x] Move the `DatabaseConnector` class to a more appropriate package (e.g., `org.estivate.db`).
- [x] Externalize database connection details (e.g., to a `.properties` file) instead of hardcoding them.

## 2. Enhance ORM Core Features
- [ ] Implement `update` operation in `DatabaseOperations` and `SimpleORM`.
- [ ] Implement `delete` operation in `DatabaseOperations` and `SimpleORM`.
- [ ] Add a `@Id` annotation to explicitly mark the primary key field in entities.
- [ ] Improve data type handling in SQL queries to support types other than strings (e.g., numbers, booleans).

## 3. Improve Project Structure and Testing
- [ ] Move the `User` entity from `src/test/java/entity/` to `src/main/java/org/estivate/example/entity/` to serve as a proper example.
- [ ] Create a `Main.java` in `src/main/java/org/estivate/example/` to demonstrate the ORM's functionality.
- [ ] Implement proper unit tests with a testing framework like JUnit.
- [ ] Configure tests to run against a test database (e.g., H2 in-memory database).

## 4. Add Documentation
- [ ] Create a `README.md` with a project description, build instructions, and usage examples.
