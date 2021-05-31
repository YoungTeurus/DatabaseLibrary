package com.github.youngteurus.servletdatabase.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DataBase {
    ResultSet executeQueryStatement(PreparedStatement preparedStatement) throws SQLException;
    int executeUpdateStatement(PreparedStatement preparedStatement) throws SQLException;

    Connection getConnection() throws DataBaseConnectionException;
}
