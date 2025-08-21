package org.estivate;

import java.sql.SQLException;

public interface DatabaseOperations {
    <T> T findById(Class<T> clazz, Object idValue) throws Exception;
    <T> void insert(T entity) throws IllegalAccessException, SQLException;
    <T> void update(T entity) throws Exception;
    <T> void delete(T entity) throws Exception;
}