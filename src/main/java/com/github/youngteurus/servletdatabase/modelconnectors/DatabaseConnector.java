package com.github.youngteurus.servletdatabase.modelconnectors;

import com.github.youngteurus.servletdatabase.database.DataBaseConnectionException;

import java.sql.SQLException;
import java.util.List;

public interface DatabaseConnector<T> {
    List<T> getAll() throws SQLException, DataBaseConnectionException;
    boolean addAndGetSuccess(T object) throws SQLException, DataBaseConnectionException;
    int removeAndGetRemovedCount(T object) throws SQLException, DataBaseConnectionException;
}
