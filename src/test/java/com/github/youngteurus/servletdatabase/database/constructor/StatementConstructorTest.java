package com.github.youngteurus.servletdatabase.database.constructor;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class StatementConstructorTest extends TestCase {

    public void testConstructSelectSQLQuery() {
    }

    public void testConstructInsertSQLQueryReturningID() {
    }

    public void testConstructInsertSQLQuery() {
    }

    public void testConstructDeleteSQLQuery() {
        List<Parameter> params = new ArrayList<>();

        params.add(new IntegerParameter("parentId", 1));
        params.add(new IntegerParameter("childId", 3));

        String sql = StatementConstructor.constructDeleteSQLQuery(
                "parents", params
        );

        System.out.println(sql);
    }
}