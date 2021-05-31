package com.github.youngteurus.servletdatabase.modelconnectors;

import com.github.youngteurus.servletdatabase.database.DataBase;
import com.github.youngteurus.servletdatabase.database.DataBaseConnectionException;
import com.github.youngteurus.servletdatabase.database.constructor.LongParameter;
import com.github.youngteurus.servletdatabase.database.constructor.Parameter;
import com.github.youngteurus.servletdatabase.database.constructor.StatementConstructor;
import com.github.youngteurus.servletdatabase.models.AbstractModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractModelDatabaseConnector<T extends AbstractModel> implements DatabaseConnector<T> {
    protected DataBase db;

    protected AbstractModelDatabaseConnector(DataBase db){
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

    public final T getById(long id) throws SQLException, DataBaseConnectionException {
        List<Parameter> params = new ArrayList<>();
        params.add(new LongParameter("id", id));

        List<T> objects = getByParameters(params);
        if(objects.size() > 0){
            return objects.get(0);
        }
        return null;
    }

    @Override
    public final List<T> getAll() throws SQLException, DataBaseConnectionException {
        @SuppressWarnings("UnnecessaryLocalVariable")
        List<T> objects = getByParameters(Collections.emptyList());

        return objects;
    }

    public final long addAndReturnId(T object) throws SQLException, DataBaseConnectionException {
        // Возвращаемые столбики SQL запроса:
        // без_названия (id)
        ResultSet rs = getResultSetOfAddedObjectId(object);
        if (rs.next()){
            return rs.getInt(1);
        }
        return -1;
    }

    private ResultSet getResultSetOfAddedObjectId(T object) throws SQLException, DataBaseConnectionException{
        Connection connection = db.getConnection();

        List<Parameter> params = getParametersForInsert(object);

        PreparedStatement ps = StatementConstructor.constructInsertStatementReturningIDFromParametersList(
                connection, getTableName(), params
        );

        return db.executeQueryStatement(ps);
    }

    protected abstract List<Parameter> getParametersForInsert(T object);

    @Override
    public int removeAndGetRemovedCount(T object) throws SQLException, DataBaseConnectionException {
        int removedCount = getResultOfRemovingObject(object);

        return removedCount;
    }

    private int getResultOfRemovingObject(T object) throws SQLException, DataBaseConnectionException {
        Connection connection = db.getConnection();

        List<Parameter> params = getParametersForRemove(object);

        PreparedStatement ps = StatementConstructor.constructRemoveStatementFromParametersList(
                connection, getTableName(), params
        );

        return db.executeUpdateStatement(ps);
    }

    protected abstract List<Parameter> getParametersForRemove(T object);
}
