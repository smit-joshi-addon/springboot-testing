package com.ntloc.demo.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ntloc.demo.AbstractTestContainerTest;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerIntegrationTest extends AbstractTestContainerTest{

	

	@Autowired
	TestRestTemplate testRestTemplate;



	@Test
	void shouldCreateCustomer() {
		// given
		CreateCustomerRequest request = new CreateCustomerRequest("name", UUID.randomUUID() + "@gmail.com", "address");
		// when
		String API_CUSTOMER_PATH = "/api/v1/customers";
		ResponseEntity<Void> createCustomerResponse = testRestTemplate.exchange(API_CUSTOMER_PATH, HttpMethod.POST,
				new HttpEntity<>(request), Void.class);
		// then
		assertThat(createCustomerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		ResponseEntity<List<Customer>> allCutomerResponse = testRestTemplate.exchange(API_CUSTOMER_PATH, HttpMethod.GET,
				null, new ParameterizedTypeReference<>() {
				});

		assertThat(allCutomerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		Customer customerCreated = Objects.requireNonNull(allCutomerResponse.getBody()).stream()
				.filter(c -> c.getEmail().equals(request.email())).findFirst().orElseThrow();

		assertThat(customerCreated.getName()).isEqualTo(request.name());
		assertThat(customerCreated.getEmail()).isEqualTo(request.email());
		assertThat(customerCreated.getAddress()).isEqualTo(request.address());
	}

	@Test
	void shouldUpdateCustomer() {
		// given
		CreateCustomerRequest request = new CreateCustomerRequest("name", UUID.randomUUID() + "@gmail.com", "address");

		String API_CUSTOMER_PATH = "/api/v1/customers";
		ResponseEntity<Void> createCustomerResponse = testRestTemplate.exchange(API_CUSTOMER_PATH, HttpMethod.POST,
				new HttpEntity<>(request), Void.class);

		assertThat(createCustomerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		ResponseEntity<List<Customer>> allCutomerResponse = testRestTemplate.exchange(API_CUSTOMER_PATH, HttpMethod.GET,
				null, new ParameterizedTypeReference<>() {
				});

		assertThat(allCutomerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		Long id = Objects.requireNonNull(allCutomerResponse.getBody()).stream()
				.filter(c -> c.getEmail().equals(request.email())).map(Customer::getId).findFirst().orElseThrow();

		String newEmail = "newEmail" + UUID.randomUUID() + "@gmail.com";

		// when
		testRestTemplate.exchange(API_CUSTOMER_PATH + "/" + id + "?email=" + newEmail, HttpMethod.PUT, null, Void.class)
				.getStatusCode().is2xxSuccessful();

		// then
		// getCustmerById
		ResponseEntity<Customer> customerByIdResponse = testRestTemplate.exchange(API_CUSTOMER_PATH + "/" + id,
				HttpMethod.GET, null, new ParameterizedTypeReference<>() {
				});

		assertThat(customerByIdResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		Customer customerUpdated = Objects.requireNonNull(customerByIdResponse.getBody());
		assertThat(customerUpdated.getName()).isEqualTo(request.name());
		assertThat(customerUpdated.getEmail()).isEqualTo(newEmail);
		assertThat(customerUpdated.getAddress()).isEqualTo(request.address());

	}

	@Test
	void shouldDeleteCustomer() {
		// given
		CreateCustomerRequest request = new CreateCustomerRequest("name", UUID.randomUUID() + "@gmail.com", "address");

		String API_CUSTOMER_PATH = "/api/v1/customers";
		ResponseEntity<Void> createCustomerResponse = testRestTemplate.exchange(API_CUSTOMER_PATH, HttpMethod.POST,
				new HttpEntity<>(request), Void.class);

		assertThat(createCustomerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		ResponseEntity<List<Customer>> allCutomerResponse = testRestTemplate.exchange(API_CUSTOMER_PATH, HttpMethod.GET,
				null, new ParameterizedTypeReference<>() {
				});

		assertThat(allCutomerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		Long id = Objects.requireNonNull(allCutomerResponse.getBody()).stream()
				.filter(c -> c.getEmail().equals(request.email())).map(Customer::getId).findFirst().orElseThrow();

		// when
		testRestTemplate.exchange(API_CUSTOMER_PATH+"/"+id, HttpMethod.DELETE, null, Void.class).getStatusCode()
				.is2xxSuccessful();
		
		// then
		ResponseEntity<Customer> customerByIdResponse = testRestTemplate.exchange(API_CUSTOMER_PATH + "/" + id,
				HttpMethod.GET, null, new ParameterizedTypeReference<>() {
				});

		assertThat(customerByIdResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

}
