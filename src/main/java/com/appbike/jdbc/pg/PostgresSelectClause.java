package com.appbike.jdbc.pg;

import org.hibernate.dialect.PostgreSQLDialect;

import jakarta.ws.rs.core.MultivaluedMap;

public class PostgresSelectClause {
	private PostgreSQLDialect dialect;
	private String table;
	private PostgresWhereClause whereClause;
	private PostgresOrderClause orderClause;
	
	public PostgresSelectClause(PostgreSQLDialect dialect, String table, MultivaluedMap<String, String> queryParams) {
		this.dialect = dialect;
		this.table = table;
		this.whereClause = new PostgresWhereClause(dialect, queryParams);
	}
	public String toQuery() {
		StringBuilder queryBuilder = new StringBuilder("SELECT * FROM ").append(table);
		
		return queryBuilder.toString();
	}
}
