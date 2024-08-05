package com.ntloc.demo.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ntloc.demo.exception.CustomerEmailUnavailableException;
import com.ntloc.demo.exception.CustomerNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

	CustomerService underTest;

	@Mock
	CustomerRepository customerRepository;

	@Captor
	ArgumentCaptor<Customer> customerArgumentCaptor;

	@BeforeEach
	void setUp() {
		underTest = new CustomerService(customerRepository);
	}

	@Test
	void shouldGetAllCustomers() {
		// given

		// when
		underTest.getCustomers();
		// then
		verify(customerRepository).findAll();
	}

	@Test
	void shouldCreateCustomer() {
		// given
		CreateCustomerRequest customerRequest = new CreateCustomerRequest("smit", "smit@gmail.com", "IND");
		// when
		underTest.createCustomer(customerRequest);
		// then
		verify(customerRepository).save(customerArgumentCaptor.capture());
		Customer customerCaptured = customerArgumentCaptor.getValue();
		assertThat(customerCaptured.getName()).isEqualTo(customerRequest.name());
		assertThat(customerCaptured.getEmail()).isEqualTo(customerRequest.email());
		assertThat(customerCaptured.getAddress()).isEqualTo(customerRequest.address());
	}

	@Test
	void shouldNotCreateCustomerANdThrowExceptionWhenCustomerFindByEmailIsPresent() {
		// given
		CreateCustomerRequest customerRequest = new CreateCustomerRequest("smit", "smit@gmail.com", "IND");
		when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(new Customer()));
		// when

		// then
		assertThatThrownBy(() -> underTest.createCustomer(customerRequest))
				.isInstanceOf(CustomerEmailUnavailableException.class)
				.hasMessage("The email " + customerRequest.email() + " unavailable.");
	}

	@Test
	void ShouldThrowNotFoundWhenGivenInvalidIDWhileUpdateCustomer() {
		// given
		Long id = 5l;
		String name = "smit";
		String email = "smit@gmail.com";
		String address = "IND";
		when(customerRepository.findById(id)).thenReturn(Optional.empty());
		// when
		// then
		assertThatThrownBy(() -> underTest.updateCustomer(id, name, email, address))
				.isInstanceOf(CustomerNotFoundException.class).hasMessage("Customer with id " + id + " doesn't found");

		verify(customerRepository, never()).save(any());
	}

	@Test
	void shouldOnlyUpdateCustomerName() {
		// given
		Long id = 5l;
		Customer customer = new Customer(id, "smit", "smit@gmail.com", "IND");
		when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
		// when
		String newName = "smit joshi";
		underTest.updateCustomer(id, newName, null, null);
		// then
		verify(customerRepository).save(customerArgumentCaptor.capture());
		Customer capturedCustomer = customerArgumentCaptor.getValue();
		assertThat(capturedCustomer.getName()).isEqualTo(newName);
		assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
		assertThat(capturedCustomer.getAddress()).isEqualTo(customer.getAddress());
	}

	@Test
	void shouldThrowEmailUnavailableWhenGivenEmailAlreadyPresentWhileUpdatingCustomer() {
		// given
		Long id = 5l;
		Customer customer = Customer.create(id, "smit", "smit@gmai.com", "IND");
		String newEmail = "smit@bing.com";
		when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
		when(customerRepository.findByEmail(newEmail)).thenReturn(Optional.of(new Customer()));
		// then
		// when
		assertThatThrownBy(() -> underTest.updateCustomer(id, null, newEmail, null))
				.isInstanceOf(CustomerEmailUnavailableException.class)
				.hasMessage("The email \"" + newEmail + "\" unavailable to update");
		verify(customerRepository, never()).save(customer);
	}

	@Test
	void shouldOnlyUpdateCustomerEmail() {
		// given
		Long id = 5l;
		Customer customer = new Customer(id, "smit", "smit@gmail.com", "IND");
		when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
		// when
		String newEmail = "smit@bing.com";
		underTest.updateCustomer(id, null, newEmail, null);
		// then
		verify(customerRepository).save(customerArgumentCaptor.capture());
		Customer capturedCustomer = customerArgumentCaptor.getValue();
		assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
		assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
		assertThat(capturedCustomer.getAddress()).isEqualTo(customer.getAddress());
	}

	@Test
	void shouldOnlyUpdateCustomerAddress() {
		// given
		Long id = 5l;
		Customer customer = new Customer(id, "smit", "smit@gmail.com", "IND");
		when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
		// then
		String newAddress = "UK";
		underTest.updateCustomer(id, null, null, newAddress);
		// then
		verify(customerRepository).save(customerArgumentCaptor.capture());
		Customer capturedCustomer = customerArgumentCaptor.getValue();
		assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
		assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
		assertThat(capturedCustomer.getAddress()).isEqualTo(newAddress);
	}

	@Test
	void shouldUpdateAllAttributesWhenUpdateCustomer() {
		// given
		Long id = 5l;
		Customer customer = new Customer(id, "smit", "smit@gmail.com", "IND");
		when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
		// when
		String newName = "Smit Joshi";
		String newEmail = "smit@bind.com";
		String newAddress = "UK";
		underTest.updateCustomer(id, newName, newEmail, newAddress);
		// then
		verify(customerRepository).save(customerArgumentCaptor.capture());
		Customer capturedCustomer = customerArgumentCaptor.getValue();
		assertThat(capturedCustomer.getName()).isEqualTo(newName);
		assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
		assertThat(capturedCustomer.getAddress()).isEqualTo(newAddress);
	}

	@Test
	void shouldThrowNotFoundWhenGivenIdDoesNotExistWhileDeletingCustomer() {
		// give
		Long id = 5l;
		when(customerRepository.existsById(id)).thenReturn(Boolean.FALSE);
		// when
		// then
		assertThatThrownBy(() -> underTest.deleteCustomer(id)).isInstanceOf(CustomerNotFoundException.class)
				.hasMessage("Customer with id " + id + " doesn't exist.");
		verify(customerRepository,never()).deleteById(id);
	}

	@Test
	void shouldDeleteCustomer() {
		// given
		Long id = 5l;
		when(customerRepository.existsById(id)).thenReturn(Boolean.TRUE);
		// when
		underTest.deleteCustomer(id);
		// then
		verify(customerRepository).deleteById(id);
	}

}
