package com.cosson.controller;

import com.cosson.entity.Customer;
import com.cosson.service.MultiTenantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MultiTenantController {

	private MultiTenantService multiTenantService;

	@GetMapping("/{id}")
	public ResponseEntity<Customer> findCustomer(@PathVariable long id) {
		return ResponseEntity.ok(multiTenantService.getCustomerEntity(id));
	}
}
