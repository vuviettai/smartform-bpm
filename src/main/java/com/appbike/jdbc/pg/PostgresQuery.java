package com.appbike.jdbc.pg;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.PostgreSQLDialect;

import com.appbike.jdbc.common.JDBCQuery;

import io.vertx.mutiny.pgclient.PgPool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class PostgresQuery extends JDBCQuery {
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

}
