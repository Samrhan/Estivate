package org.estivate;

import org.estivate.db.DatabaseConnector;
import org.estivate.annotations.Column;
import org.estivate.annotations.Id;
import org.estivate.annotations.Table;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleORM implements DatabaseOperations {

    @Override
    public <T> void insert(T entity) throws IllegalAccessException, SQLException {
        Class<?> clazz = entity.getClass();
        Table tableAnnotation = clazz.getAnnotation(Table.class);
        String tableName = tableAnnotation.name();

        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();
        List<Object> values = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Column.class)) {
                Column columnAnnotation = field.getAnnotation(Column.class);
                columns.append(columnAnnotation.name()).append(",");
                placeholders.append("?,");
                values.add(field.get(entity));
            }
        }

        columns.deleteCharAt(columns.length() - 1);
        placeholders.deleteCharAt(placeholders.length() - 1);

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
                tableName, columns, placeholders);

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            stmt.executeUpdate();
        }
    }

    @Override
    public <T> T findById(Class<T> clazz, Object idValue) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        T entity = clazz.getDeclaredConstructor().newInstance();

        try {
            conn = DatabaseConnector.getConnection();
            Table tableAnnotation = clazz.getAnnotation(Table.class);

            Field idField = null;
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Id.class)) {
                    idField = field;
                    break;
                }
            }

            if (idField == null) {
                throw new Exception("No @Id annotation found in " + clazz.getName());
            }

            String idColumn = idField.getAnnotation(Column.class).name();

            String sql = String.format("SELECT * FROM %s WHERE %s = ?", tableAnnotation.name(), idColumn);
            stmt = conn.prepareStatement(sql);
            stmt.setObject(1, idValue);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    Column columnAnnotation = field.getAnnotation(Column.class);
                    if (columnAnnotation != null) {
                        Object value = rs.getObject(columnAnnotation.name());
                        field.set(entity, value);
                    }
                }
                return entity;
            }
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
        return null;
    }

    @Override
    public <T> void update(T entity) throws Exception {
        Class<?> clazz = entity.getClass();
        Table tableAnnotation = clazz.getAnnotation(Table.class);
        String tableName = tableAnnotation.name();

        Field idField = null;
        Object idValue = null;
        StringBuilder setClause = new StringBuilder();
        List<Object> values = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Id.class)) {
                idField = field;
                idValue = field.get(entity);
            } else if (field.isAnnotationPresent(Column.class)) {
                Column columnAnnotation = field.getAnnotation(Column.class);
                setClause.append(columnAnnotation.name()).append(" = ?,");
                values.add(field.get(entity));
            }
        }

        if (idField == null) {
            throw new Exception("No @Id annotation found in " + clazz.getName());
        }

        setClause.deleteCharAt(setClause.length() - 1); // Remove the last comma

        String idColumnName = idField.getAnnotation(Column.class).name();
        String sql = String.format("UPDATE %s SET %s WHERE %s = ?",
                tableName, setClause, idColumnName);

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int i = 0;
            for (; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            stmt.setObject(i + 1, idValue);
            stmt.executeUpdate();
        }
    }

    @Override
    public <T> void delete(T entity) throws Exception {
        Class<?> clazz = entity.getClass();
        Table tableAnnotation = clazz.getAnnotation(Table.class);
        String tableName = tableAnnotation.name();

        Field idField = null;
        Object idValue = null;

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Id.class)) {
                idField = field;
                idValue = field.get(entity);
                break;
            }
        }

        if (idField == null) {
            throw new Exception("No @Id annotation found in " + clazz.getName());
        }

        String idColumnName = idField.getAnnotation(Column.class).name();
        String sql = String.format("DELETE FROM %s WHERE %s = ?",
                tableName, idColumnName);

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, idValue);
            stmt.executeUpdate();
        }
    }
}