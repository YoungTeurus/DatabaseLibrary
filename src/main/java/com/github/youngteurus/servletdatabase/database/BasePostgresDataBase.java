package com.github.youngteurus.servletdatabase.database;

import java.sql.*;

public class BasePostgresDataBase implements DataBase {
    private static BasePostgresDataBase instance;
    private static BasePostgresDataBase creditServiceInstance;

    private final String dbURL;
    private final String user;
    private final String pass;


    public BasePostgresDataBase(String dbURL, String user, String pass) {
        this.dbURL = dbURL;
        this.user = user;
        this.pass = pass;
        if (instance == null) {
            instance = this;
        }
    }

    static Connection connectionInstance;

    private void createConnectionInstance() throws DataBaseConnectionException{
        System.out.println("Testing connection to PostgreSQL JDBC");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new DataBaseConnectionException("PostgreSQL JDBC Driver is not found");
        }

        System.out.println("PostgreSQL JDBC Driver successfully connected");
        connectionInstance = null;

        try {
            connectionInstance = DriverManager
                    .getConnection(dbURL, user, pass);

        } catch (SQLException e) {
            throw new DataBaseConnectionException(String.format("Connection Failed. dbURL = %s, user = %s, pass = %s", dbURL, user, pass));
        }
    }

    public ResultSet executeStatement(PreparedStatement preparedStatement) throws SQLException {
        return preparedStatement.executeQuery();
    }

    public Connection getConnection() throws DataBaseConnectionException {
        if (connectionInstance == null) {
            createConnectionInstance();
        }
        return connectionInstance;
    }
}
