package com.github.youngteurus.servletdatabase.modelconnectors;

import com.github.youngteurus.servletdatabase.database.DataBase;
import com.github.youngteurus.servletdatabase.database.DataBaseConnectionException;
import com.github.youngteurus.servletdatabase.database.constructor.LongParameter;
import com.github.youngteurus.servletdatabase.database.constructor.Parameter;
import com.github.youngteurus.servletdatabase.database.constructor.StatementConstructor;
import com.github.youngteurus.servletdatabase.models.AbstractModel;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractModelDatabaseConnector<T extends AbstractModel> extends BaseDatabaseConnector<T> {
    protected DataBase db;

    protected AbstractModelDatabaseConnector(DataBase db){
        super(db);
    }

    public final T getById(long id) throws SQLException, DataBaseConnectionException {
        List<Parameter> params = new ArrayList<>();
        params.add(new LongParameter("id", id));

        List<T> objects = getByParameters(params);

        if(objects.size() > 0){
            return objects.get(0);
        }
        return null;
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

    public final boolean removeById(long id) throws SQLException, DataBaseConnectionException {
        List<Parameter> params = new ArrayList<>();
        params.add(new LongParameter("id", id));

        int removeCount = removeByParameters(params);

        return removeCount > 0;
    }

    private ResultSet getResultSetOfAddedObjectId(T object) throws SQLException, DataBaseConnectionException{
        Connection connection = db.getConnection();

        List<Parameter> params = getParametersForInsert(object);

        PreparedStatement ps = StatementConstructor.constructInsertStatementReturningIDFromParametersList(
                connection, getTableName(), params
        );

        return db.executeQueryStatement(ps);
    }
}
