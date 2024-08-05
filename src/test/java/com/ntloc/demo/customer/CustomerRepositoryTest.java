package com.ntloc.demo.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ntloc.demo.AbstractTestContainerTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CustomerRepositoryTest extends AbstractTestContainerTest {

	@Autowired
	CustomerRepository underTest;

	@BeforeEach
	void setUp() {
		Customer customer = Customer.create("smit", "smit@gmail.com", "IND");
		underTest.save(customer);
	}

	@AfterEach
	void tearDown() {
		underTest.deleteAll();
	}

	@Test
	void shouldReturnCustomerWhenFindByEmail() {
		// given

		// when
		Optional<Customer> customerByEmail = underTest.findByEmail("smit@gmail.com");
		// then
		assertThat(customerByEmail).isPresent();
	}

	@Test
	void shouldNotCustomerWhenFindByEmailIsNotPresent() {
		// given
		Customer customer = Customer.create("demo", "demo1@gmail.com", "IND");
		underTest.save(customer);
		// when
		Optional<Customer> customerByEmail = underTest.findByEmail("demo@mail.com");
		// then
		assertThat(customerByEmail).isNotPresent();
	}

}
