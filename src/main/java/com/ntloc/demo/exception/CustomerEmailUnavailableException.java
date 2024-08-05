package com.ntloc.demo.exception;

public class CustomerEmailUnavailableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

	public CustomerEmailUnavailableException(String message) {
        super(message);
    }
}
