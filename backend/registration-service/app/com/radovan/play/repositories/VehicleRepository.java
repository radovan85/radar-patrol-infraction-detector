package com.radovan.play.repositories;

import com.radovan.play.entity.VehicleEntity;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository {

    Optional<VehicleEntity> findById(Long vehicleId);

    VehicleEntity save(VehicleEntity vehicleEntity);

    void deleteById(Long vehicleId);

    List<VehicleEntity> findAll();

    Long count();

    Optional<VehicleEntity> findByRegistrationNumber(String registrationNumber);

    List<VehicleEntity> findByRegistrationNumberContaining(String keyword);

    List<VehicleEntity> findAllByOwnerId(Long ownerId);
}
