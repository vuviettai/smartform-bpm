package com.appbike.jdbc.pg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.PostgreSQLDialect;

import com.appbike.jdbc.common.JDBCQuery;

import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.core.MultivaluedMap;

@ApplicationScoped
public class PostgresQuery extends JDBCQuery {
	public static String PGQUERY_GET_ALL_TABLES= "SELECT * FROM pg_catalog.pg_tables WHERE schemaname='public';";
    @Inject
    private	PgPool client;
    
    PostgreSQLDialect dialect = new PostgreSQLDialect();
    @Inject
    EntityManager em;

	@Override
	protected Dialect getDialect() {
		// TODO Auto-generated method stub
		return dialect;
	}
	@Override
	protected io.vertx.mutiny.sqlclient.Pool getClientPool() {
		return client;
	}
	@Override
	protected EntityManager getEntityManager() {
		// TODO Auto-generated method stub
		return em;
	}
	private Map<String, Object> getRowData(Row row) {
		int size = row.size();
		Map<String, Object> data = new HashMap<String, Object>();
		for (int i = 0; i < size; i++) {
			String columnName = row.getColumnName(i);
	        Object columnValue = row.getValue(i);
	        data.put(columnName, columnValue);
		}
		return data;
	}
	public List<Map<String, Object>> queryCollection(String collection, MultivaluedMap<String, String> queryParams) {
		PostgresSelectClause query = new PostgresSelectClause(dialect, collection, queryParams);
		String sql = query.toQuery();
		Multi<Map<String, Object>> rows = execute(sql, this::getRowData);
		return rows.collect().asList().await().indefinitely();
	}
	public List<String> getAllTables() {
		List<String> tables = new ArrayList<String>();
		Multi<String> rows = execute(PGQUERY_GET_ALL_TABLES, row->row.getString("tablename"));
		if (rows != null) {
			tables = rows.collect().asList().await().indefinitely();
		}
		return tables;
	}
	
}
