package com.smartform.repository;

import java.util.Optional;

import com.smartform.domain.Service;
import com.smartform.domain.User;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User>{
	public Optional<User> findByCode(String code) {
        return Optional.ofNullable(find("code", code).firstResult());
    }
}
