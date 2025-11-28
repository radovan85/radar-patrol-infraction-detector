package com.radovan.spring.repositories;

import com.radovan.spring.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

	List<UserEntity> findAll();

	Optional<UserEntity> findById(Long userId);

	Optional<UserEntity> findByEmail(String email);

	UserEntity save(UserEntity userEntity);

	void deleteById(Long userId);
}
