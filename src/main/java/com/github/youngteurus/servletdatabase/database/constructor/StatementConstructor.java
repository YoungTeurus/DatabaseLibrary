package com.github.youngteurus.servletdatabase.database.constructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class StatementConstructor {
    public static PreparedStatement constructSelectStatementFromParametersList(Connection connection, String tableName, List<Parameter> parameters) throws SQLException {
        String sql = constructSelectSQLQuery(tableName, parameters);
        return prepareStatement(connection, sql, parameters);
    }

    public static PreparedStatement constructInsertStatementFromParametersList(Connection connection, String tableName, List<Parameter> parameters) throws SQLException {
        String sql = constructInsertSQLQuery(tableName, parameters);
        return prepareStatement(connection, sql, parameters);
    }

    public static PreparedStatement constructInsertStatementReturningIDFromParametersList(Connection connection, String tableName, List<Parameter> parameters) throws SQLException {
        String sql = constructInsertSQLQueryReturningID(tableName, parameters);
        return prepareStatement(connection, sql, parameters);
    }

    public static PreparedStatement constructDeleteStatementFromParametersList(Connection connection, String tableName, List<Parameter> parameters) throws SQLException {
        String sql = constructDeleteSQLQuery(tableName, parameters);
        return prepareStatement(connection, sql, parameters);
    }

    // Методы оставлены package-видимости для тестов.
    static String constructSelectSQLQuery(String tableName, List<Parameter> parameters){
        StringBuilder sql = new StringBuilder();
        // Составляем sql запрос по типу:
        // SELECT * FROM public."users" WHERE param_1 = ? AND param_2 = ? AND param_3 = ?
        sql.append("SELECT * FROM public.").append("\"").append(tableName).append("\"");
        if (parameters.size() > 0 ){
            int parametersCount = parameters.size();
            int currentParameter = 0;

            sql.append(" WHERE");
            for (Parameter param : parameters) {
                String SQLParameter = param.getParameter();
                sql.append(" \"").append(SQLParameter).append("\" = ?");
                if (currentParameter != parametersCount - 1){
                    sql.append(" AND");
                }
                currentParameter++;
            }
        }
        return sql.toString();
    }

    static String constructInsertSQLQueryReturningID(String tableName, List<Parameter> parameters){
        // Составляем sql запрос по типу:
        // INSERT INTO public."users" ("param_1","param_2","param_3") Values (?,?,?) RETURNING id
        String insertSQL = constructInsertSQLQuery(tableName, parameters);
        insertSQL += " RETURNING id";

        return insertSQL;
    }

    static String constructInsertSQLQuery(String tableName, List<Parameter> parameters){
        if (parameters.size() == 0){
            throw new ConstructorException("StatementConstructor::constructInsertSQLQuery: не было передано ни одного параметра.");
        }

        StringBuilder sql = new StringBuilder();

        // Составляем sql запрос по типу:
        // INSERT INTO public."users" ("param_1","param_2","param_3") Values (?,?,?)
        sql.append("INSERT INTO public.").append("\"").append(tableName).append("\" ")
                .append("(");

        int parametersCount = parameters.size();
        int currentParameter = 0;

        for (Parameter param : parameters) {
            String SQLParameter = param.getParameter();

            sql.append("\"").append(SQLParameter).append("\"");
            if (currentParameter < parametersCount - 1){
                sql.append(",");
            }
            currentParameter++;
        }

        sql.append(") Values (");

        currentParameter = 0;
        for (Parameter param : parameters) {
            sql.append("?");
            if (currentParameter < parametersCount - 1){
                sql.append(",");
            }
            currentParameter++;
        }

        sql.append(")");

        return sql.toString();
    }

    static String constructDeleteSQLQuery(String tableName, List<Parameter> parameters){
        // Да, мы можем захотеть удалить всю таблицу, но здесь такое не позволяем.
        if (parameters.size() == 0){
            throw new ConstructorException("StatementConstructor::constructRemoveSQLQuery: не было передано ни одного параметра.");
        }

        StringBuilder sql = new StringBuilder();

        // Составляем sql запрос по типу:
        // DELETE FROM public."parents" WHERE "param_1" = ? AND "param_2" = ? AND "param_3" = ?
        sql.append("DELETE FROM public.").append("\"").append(tableName).append("\"");

        // TODO: возможно ли убрать здесь дубликат кода создания блока WHERE?
        int parametersCount = parameters.size();
        int currentParameter = 0;

        sql.append(" WHERE");
        for (Parameter param : parameters) {
            String SQLParameter = param.getParameter();
            sql.append(" \"").append(SQLParameter).append("\" = ?");
            if (currentParameter != parametersCount - 1){
                sql.append(" AND");
            }
            currentParameter++;
        }

        return sql.toString();
    }

    static PreparedStatement prepareStatement(Connection connection, String sql, List<Parameter> parameters) throws SQLException{
        PreparedStatement ps = connection.prepareStatement(sql);

        int paramIndex = 1;
        for(Parameter param : parameters){
            param.applyParameter(ps, paramIndex++);
        }

        return ps;
    }
}
