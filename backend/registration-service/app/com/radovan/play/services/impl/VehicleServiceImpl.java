package com.radovan.play.services.impl;

import com.radovan.play.converter.TempConverter;
import com.radovan.play.dto.VehicleDto;
import com.radovan.play.entity.VehicleEntity;
import com.radovan.play.exceptions.ExistingInstanceException;
import com.radovan.play.exceptions.InstanceUndefinedException;
import com.radovan.play.repositories.VehicleRepository;
import com.radovan.play.services.OwnerService;
import com.radovan.play.services.VehicleService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class VehicleServiceImpl implements VehicleService {

    private VehicleRepository vehicleRepository;
    private TempConverter tempConverter;
    private OwnerService ownerService;



    @Inject
    private void initialize(VehicleRepository vehicleRepository, TempConverter tempConverter, OwnerService ownerService) {
        this.vehicleRepository = vehicleRepository;
        this.tempConverter = tempConverter;
        this.ownerService = ownerService;
    }

    @Override
    public VehicleDto addVehicle(VehicleDto vehicleDto) {
        ownerService.getOwnerById(vehicleDto.getOwnerId());
        Optional<VehicleEntity> existingvehicleOptional = vehicleRepository.findByRegistrationNumber(vehicleDto.getRegistrationNumber());
        if (existingvehicleOptional.isPresent()) {
            throw new ExistingInstanceException("This registration number already exists!");
        }

        VehicleEntity storedVehicle = vehicleRepository.save(tempConverter.vehicleDtoToEntity(vehicleDto));
        return tempConverter.vehicleEntityToDto(storedVehicle);
    }

    @Override
    public VehicleDto updateVehicle(VehicleDto vehicleDto, Long vehicleId) {
        ownerService.getOwnerById(vehicleDto.getOwnerId());
        Optional<VehicleEntity> existingvehicleOptional = vehicleRepository.findByRegistrationNumber(vehicleDto.getRegistrationNumber());
        if (existingvehicleOptional.isPresent()) {
            VehicleEntity existingVehicle = existingvehicleOptional.get();
            if (!Objects.equals(existingVehicle.getId(), vehicleId)) {
                throw new ExistingInstanceException("This registration number already exists!");
            }
        }
        vehicleDto.setId(vehicleId);
        VehicleEntity updatedVehicle = vehicleRepository.save(tempConverter.vehicleDtoToEntity(vehicleDto));
        return tempConverter.vehicleEntityToDto(updatedVehicle);
    }

    @Override
    public VehicleDto getVehicleById(Long vehicleId) {
        VehicleEntity vehicleEntity = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new InstanceUndefinedException("The vehicle has not been found!"));
        return tempConverter.vehicleEntityToDto(vehicleEntity);
    }

    @Override
    public void deleteVehicle(Long vehicleId) {
        getVehicleById(vehicleId);
        vehicleRepository.deleteById(vehicleId);
    }

    @Override
    public Long vehicleCount() {
        return vehicleRepository.count();
    }

    @Override
    public List<VehicleDto> listAll() {
        return vehicleRepository.findAll()
                .stream().map(tempConverter::vehicleEntityToDto).collect(Collectors.toList());
    }

    @Override
    public List<VehicleDto> listAllByRegistrationNumberContains(String keyword) {
        return vehicleRepository.findByRegistrationNumberContaining(keyword)
                .stream().map(tempConverter::vehicleEntityToDto).collect(Collectors.toList());
    }

    @Override
    public VehicleDto getVehicleByRegistrationNumber(String registrationNumber) {
        VehicleEntity vehicleEntity = vehicleRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new InstanceUndefinedException("The vehicle has not been found!"));
        return tempConverter.vehicleEntityToDto(vehicleEntity);
    }
}
