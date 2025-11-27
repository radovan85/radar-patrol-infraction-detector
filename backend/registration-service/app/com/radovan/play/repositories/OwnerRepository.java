package com.radovan.play.repositories;

import com.radovan.play.entity.OwnerEntity;

import java.util.List;
import java.util.Optional;

public interface OwnerRepository {

    Optional<OwnerEntity> findById(Long ownerId);

    Optional<OwnerEntity> findByEmail(String email);

    OwnerEntity save(OwnerEntity ownerEntity);

    void deleteById(Long ownerId);

    List<OwnerEntity> findAll();

    Long count();
}
