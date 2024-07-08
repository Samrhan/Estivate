package org.estivate;

import java.sql.SQLException;

public interface DatabaseOperations {
    <T> T findById(Class<T> clazz, Object idValue) throws Exception;
    <T> void insert(T entity) throws IllegalAccessException, SQLException;
}