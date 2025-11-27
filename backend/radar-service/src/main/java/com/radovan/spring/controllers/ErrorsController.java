package com.radovan.spring.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.exceptions.InstanceUndefinedException;

@RestControllerAdvice
public class ErrorsController {

	@ExceptionHandler(DataNotValidatedException.class)
	public ResponseEntity<String> handleDataNotValidatedException(DataNotValidatedException exc) {
		return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InstanceUndefinedException.class)
	public ResponseEntity<String> handleInstanceUndefinedException(InstanceUndefinedException exc) {
		return new ResponseEntity<>(exc.getMessage(), HttpStatus.PRECONDITION_FAILED);
	}
}
