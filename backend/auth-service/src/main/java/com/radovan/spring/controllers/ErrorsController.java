package com.radovan.spring.controllers;

import javax.security.auth.login.CredentialNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.exceptions.ExistingInstanceException;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.exceptions.OperationNotAllowedException;
import com.radovan.spring.exceptions.SuspendedUserException;

@RestControllerAdvice
public class ErrorsController {

	@ExceptionHandler(DataNotValidatedException.class)
	public ResponseEntity<String> handleDataNotValidatedException(DataNotValidatedException exc) {
		return new ResponseEntity<String>(exc.getMessage(), HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(ExistingInstanceException.class)
	public ResponseEntity<String> handleExistingInstanceException(ExistingInstanceException exc) {
		return new ResponseEntity<String>(exc.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(SuspendedUserException.class)
	public ResponseEntity<String> handleSuspendedUserException(SuspendedUserException exc) {
		SecurityContextHolder.clearContext();
		return new ResponseEntity<String>(exc.getMessage(), HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);
	}

	@ExceptionHandler(InstanceUndefinedException.class)
	public ResponseEntity<String> handleInstanceUndefinedException(InstanceUndefinedException exc) {
		return new ResponseEntity<String>(exc.getMessage(), HttpStatus.PRECONDITION_FAILED);
	}

	@ExceptionHandler(CredentialNotFoundException.class)
	public ResponseEntity<String> handleCredentialNotFoundException(CredentialNotFoundException exc) {
		return new ResponseEntity<String>(exc.getMessage(), HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(OperationNotAllowedException.class)
	public ResponseEntity<String> handleOperationNotAllowedException(OperationNotAllowedException exc) {
		return new ResponseEntity<String>(exc.getMessage(), HttpStatus.NOT_ACCEPTABLE);
	}
}