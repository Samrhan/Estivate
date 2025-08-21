package org.estivate;

import org.estivate.db.DatabaseConnector;
import org.estivate.annotations.Column;
import org.estivate.annotations.Table;

import java.lang.reflect.Field;
import java.sql.*;

public class SimpleORM implements DatabaseOperations {

    @Override
    public <T> void insert(T entity) throws IllegalAccessException, SQLException {
        Class<?> clazz = entity.getClass();
        Table tableAnnotation = clazz.getAnnotation(Table.class);
        String tableName = tableAnnotation.name();

        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true); // to access private fields
            Column columnAnnotation = field.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                columns.append(columnAnnotation.name()).append(",");
                values.append("'").append(field.get(entity)).append("'").append(",");
            }
        }

        columns.deleteCharAt(columns.length() - 1); // Remove the last comma
        values.deleteCharAt(values.length() - 1); // Remove the last comma

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
                tableName, columns, values);
        System.out.println(sql);
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement()) {
             stmt.executeUpdate(sql);
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

            // Assuming the first field is always the ID. Better approach would be marking the ID field explicitly.
            Field idField = clazz.getDeclaredFields()[0];
            idField.setAccessible(true);
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
}