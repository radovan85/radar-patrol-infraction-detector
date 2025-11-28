package com.radovan.spring.services;

import com.radovan.spring.dto.UserDto;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface UserService {

	List<UserDto> listAll();

	UserDto getCurrentUser();

	UserDto getUserById(Long userId);

	UserDto getUserByEmail(String email);

	Optional<Authentication> authenticateUser(String username, String password);

	Boolean isAdmin();

	Boolean isAdmin(Long userId);

	void suspendUser(Long userId);

	void reactivateUser(Long userId);

	UserDto addUser(UserDto user);

	void deleteUser(Long userId);

}