package com.github.youngteurus.servletdatabase.modelconnectors;

import com.github.youngteurus.servletdatabase.database.DataBase;
import com.github.youngteurus.servletdatabase.database.DataBaseConnectionException;
import com.github.youngteurus.servletdatabase.database.constructor.Parameter;
import com.github.youngteurus.servletdatabase.database.constructor.StatementConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseDatabaseConnector<T> implements DatabaseConnector<T> {
    protected DataBase db;

    protected BaseDatabaseConnector(DataBase db){
        this.db = db;
    }

    protected abstract String getTableName();

    public List<T> getByParameters(List<Parameter> parameters) throws SQLException, DataBaseConnectionException{
        ResultSet resultSet = getResultSetOfFoundObjectsByParameters(parameters);

        List<T> objects = new ArrayList<>();
        while (resultSet.next()) {
            objects.add(constructObjectFromResultSet(resultSet));
        }
        return objects;
    }

    private ResultSet getResultSetOfFoundObjectsByParameters(List<Parameter> parameters) throws SQLException, DataBaseConnectionException{
        Connection connection = db.getConnection();

        PreparedStatement preparedStatement = StatementConstructor.constructSelectStatementFromParametersList(
                connection, getTableName(), parameters
        );

        return db.executeQueryStatement(preparedStatement);
    }

    protected abstract T constructObjectFromResultSet(ResultSet rs);

    @Override
    public final List<T> getAll() throws SQLException, DataBaseConnectionException {
        @SuppressWarnings("UnnecessaryLocalVariable")
        List<T> objects = getByParameters(Collections.emptyList());

        return objects;
    }

    public final boolean addAndGetSuccess(T object) throws SQLException, DataBaseConnectionException {
        List<Parameter> params = getParametersForInsert(object);

        int result = addByParameters(params);

        return result > 0;
    }

    protected final int addByParameters(List<Parameter> parameters) throws SQLException, DataBaseConnectionException{
        Connection connection = db.getConnection();

        PreparedStatement ps = StatementConstructor.constructInsertStatementFromParametersList(
                connection, getTableName(), parameters
        );

        return db.executeUpdateStatement(ps);
    }

    protected abstract List<Parameter> getParametersForInsert(T object);

    public final int removeAndGetRemovedCount(T object) throws SQLException, DataBaseConnectionException {
        List<Parameter> params = getParametersForRemove(object);

        int removeCount = removeByParameters(params);

        return removeCount;
    }

    protected final int removeByParameters(List<Parameter> parameters) throws SQLException, DataBaseConnectionException {
        Connection connection = db.getConnection();

        PreparedStatement ps = StatementConstructor.constructDeleteStatementFromParametersList(
                connection, getTableName(), parameters
        );

        int removeCount = db.executeUpdateStatement(ps);
        return removeCount;
    }

    protected abstract List<Parameter> getParametersForRemove(T object);
}
