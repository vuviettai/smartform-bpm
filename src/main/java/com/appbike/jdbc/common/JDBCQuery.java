package com.appbike.jdbc.common;

import java.io.IOException;
import java.util.function.Function;

import org.hibernate.dialect.Dialect;
import org.hibernate.sql.ast.spi.StringBuilderSqlAppender;

import com.appbike.jdbc.IJDBCQuery;
import com.smartform.models.RequestParams;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import jakarta.persistence.EntityManager;

public abstract class JDBCQuery implements IJDBCQuery {
    abstract protected Dialect getDialect(); 
    abstract protected io.vertx.mutiny.sqlclient.Pool getClientPool();
    abstract protected EntityManager getEntityManager();
    protected String getTableName(String entityName) {
    	return entityName + "s";
    }
    public String getSelectClause() {
    	return "SELECT * ";
    }
    public String getFromClause(String tableName) {
    	Dialect dialect = getDialect();
        return " FROM " + dialect.openQuote() + tableName + dialect.closeQuote();
    }
    public String createQuery(String entityName, RequestParams params) {
    	StringBuilderSqlAppender sqlBuilder = new StringBuilderSqlAppender();
    	String tableName = getTableName(entityName);
    	String fromClause = getFromClause(tableName);
    	String selectClause = getSelectClause();
    	try {
			sqlBuilder.append(selectClause)
					.append(fromClause);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return sqlBuilder.toString();
    }
    public<O> Multi<O> execute(String query, Function<? super Row, ? extends O> mapper) {
    	Multi<O> result = null;
    	try {
    		Pool pool = this.getClientPool();
	    	Uni<RowSet<Row>> uniRows = pool.query(query).execute();
	    	result = uniRows
	    			  .onItem().transformToMulti(set -> Multi.createFrom().iterable(set)).map(mapper);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return result;
    }
    public Multi<Row> execute(String entityName, String query) {
    	Multi<Row> result = null;
    	try {
    		Pool pool = this.getClientPool();
	    	Uni<RowSet<Row>> uniRows = pool.query(query).execute();
	    	result = uniRows
	    			  .onItem().transformToMulti(set -> Multi.createFrom().iterable(set));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return result;
    }
}
