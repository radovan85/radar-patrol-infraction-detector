package com.radovan.play.services;

import com.radovan.play.dto.VehicleDto;

import java.util.List;

public interface VehicleService {

    VehicleDto addVehicle(VehicleDto vehicleDto);

    VehicleDto updateVehicle(VehicleDto vehicleDto,Long vehicleId);

    VehicleDto getVehicleById(Long vehicleId);

    void deleteVehicle(Long vehicleId);

    Long vehicleCount();

    List<VehicleDto> listAll();

    List<VehicleDto> listAllByRegistrationNumberContains(String keyword);

    VehicleDto getVehicleByRegistrationNumber(String registrationNumber);
}
