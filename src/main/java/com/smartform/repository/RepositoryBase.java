package com.smartform.repository;

import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

public interface RepositoryBase<Entity> extends PanacheRepositoryBase<Entity, UUID> {

}