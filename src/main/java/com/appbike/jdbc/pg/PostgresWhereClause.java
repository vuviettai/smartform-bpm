package com.appbike.jdbc.pg;

import org.hibernate.dialect.PostgreSQLDialect;

import jakarta.ws.rs.core.MultivaluedMap;

public class PostgresWhereClause {

	private PostgreSQLDialect dialect;
	private MultivaluedMap<String, String> queryParams;
	public PostgresWhereClause(PostgreSQLDialect dialect, MultivaluedMap<String, String> queryParams) {
		this.dialect = dialect;
		this.queryParams = queryParams;
	}
	public String toQuery() {
		return "";
	}
}
