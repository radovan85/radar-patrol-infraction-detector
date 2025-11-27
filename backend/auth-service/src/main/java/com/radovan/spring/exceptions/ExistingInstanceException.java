package com.radovan.spring.exceptions;

public class ExistingInstanceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ExistingInstanceException(String message) {
		super(message);
	}

}