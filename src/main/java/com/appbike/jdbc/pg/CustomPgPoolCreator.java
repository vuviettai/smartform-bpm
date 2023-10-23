package com.appbike.jdbc.pg;

import jakarta.inject.Singleton;

import java.util.List;

import io.quarkus.reactive.pg.client.PgPoolCreator;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

@Singleton
public class CustomPgPoolCreator implements PgPoolCreator {

    @Override
    public PgPool create(Input input) {
        List<PgConnectOptions> connectOptions = input.pgConnectOptionsList();
        PoolOptions poolOptions = input.poolOptions();
        // Customize connectOptions, poolOptions or both, as required
        return PgPool.pool(input.vertx(), connectOptions, poolOptions);
    }
}