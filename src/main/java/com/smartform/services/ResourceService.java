package com.smartform.services;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.appbike.jdbc.common.JDBCQuery;
import com.appbike.jdbc.pg.PostgresQuery;
import com.smartform.domain.EntityBase;
import com.smartform.domain.GenericEntity;
import com.smartform.domain.dto.ResourceDto;
import com.smartform.mapping.PropertiesTransform;
import com.smartform.models.RequestParams;
import com.smartform.repository.CustomerRepository;
import com.smartform.repository.ResourceRepository;
import com.smartform.repository.UserRepository;

import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ResourceService {
	public static final String METHOD_GET_ENTITY_MANAGER= "getEntityManager";
	public static final String METHOD_WAIT	 			= "wait";
	public static final String METHOD_EQUALS 			= "equals";
	public static final String METHOD_TOSTRING 			= "toString";
	public static final String METHOD_HASHCODE 			= "hashCode";
	public static final String METHOD_GETCLASS 			= "getClass";
	public static final String METHOD_NOTIFY 			= "notify";
	public static final String METHOD_NOTIFYALL			= "notifyAll";
	
	public static final String METHOD_COUNT				= "count";
	public static final String METHOD_DELETE			= "delete";
	public static final String METHOD_DELETEALL			= "deleteAll";
	public static final String METHOD_DELETEBYID		= "deleteById";
	public static final String METHOD_FIND 				= "find";
	public static final String METHOD_FINDALL			= "findAll";
	public static final String METHOD_FINDBYID			= "findById";
	public static final String METHOD_FLUSH				= "flush";
	public static final String METHOD_LIST 				= "list";
	public static final String METHOD_LISTALL			= "listAll";
	public static final String METHOD_PERSIST			= "persist";
	public static final String METHOD_PERSISTANDFLUSH	= "persistAndFlush";
	public static final String METHOD_ISPERSISTENT		= "isPersistent";
	public static final String METHOD_STREAM 			= "stream";
	public static final String METHOD_STREAMALL			= "streamAll";
	public static final String METHOD_UPDATE 			= "update";
	
	
	// @Inject
	// ApplicationContext applicationContext;
	@Inject
	BeanManager beanManager;
	@Inject
	UserRepository repository;
	@Inject
	CustomerRepository customerRepository;
	@Inject
	ResourceRepository resRepository;
	@Inject
	PostgresQuery pgQuery;
	@Inject
	PgPool client;
	private Bean<?> getBeanByName(String beanName) {
		Bean<?> bean = null;
		Set<Bean<?>> beans = beanManager.getBeans(beanName);
		if (beans.size() > 0) {
			bean = beans.iterator().next();
		}
		return bean;
	}
	private Method getBeanMethod(Bean<?> bean, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
		return bean.getBeanClass().getMethod(methodName, parameterTypes);
	}
	public <E extends EntityBase> List<EntityBase> list(String entityName) {
		List<EntityBase> result = null;
		Bean<?> bean = getBeanByName(entityName);
		if (bean != null) {
			try {
				Method method = getBeanMethod(bean, METHOD_LISTALL);
				for(Method m : bean.getClass().getMethods()) {
					System.out.println(m.getName());
				}
				//Object value = method.invoke(bean);
				//System.out.println(value);
			} catch (NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
//			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		return result;
	}
	
	public List<Map<String, Object>> find(String entityName, RequestParams params) {
		List<Map<String, Object>> result = null;
		//List<EntityBase> list = this.list(entityName);
		String query = ((JDBCQuery) pgQuery).createQuery(entityName, params);
		//resRepository.list(query, (Sort) null);
		Multi<Row> rows = pgQuery.execute(entityName, query);
		result = rows.onItem().transform(PropertiesTransform::from)
				.collect().asList().await().indefinitely();
		return result;
	}
	
	@Transactional
	public <E extends EntityBase> EntityBase persist(E entity) {
		entity.persist();
		return entity;
	}
	@Transactional
	public <E extends EntityBase> EntityBase persist(ResourceDto resDto) {
		
		return null;
	}
	
}
