package com.radovan.spring.exceptions;

public class SuspendedUserException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SuspendedUserException(String message) {
		super(message);

	}

}