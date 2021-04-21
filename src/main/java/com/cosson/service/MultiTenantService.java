package com.cosson.service;

import com.cosson.entity.Customer;
import com.cosson.repo.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MultiTenantService {

	private final CustomerRepo customerRepo;

	@Autowired
	public MultiTenantService(CustomerRepo customerRepo) {
		this.customerRepo = customerRepo;
	}

	public Customer getCustomerEntity(long id) {
		return customerRepo.findById(id).orElse(null);
	}
}
