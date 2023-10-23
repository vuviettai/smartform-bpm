package com.smartform.repository;

import com.smartform.domain.Customer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@ApplicationScoped
@Named("customer")
public class CustomerRepository implements RepositoryBase<Customer> {

}
