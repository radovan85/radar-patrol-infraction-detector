package com.radovan.play.services.impl;

import com.radovan.play.converter.TempConverter;
import com.radovan.play.dto.OwnerDto;
import com.radovan.play.entity.OwnerEntity;
import com.radovan.play.exceptions.ExistingInstanceException;
import com.radovan.play.exceptions.InstanceUndefinedException;
import com.radovan.play.repositories.OwnerRepository;
import com.radovan.play.services.OwnerService;
import com.radovan.play.services.VehicleService;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class OwnerServiceImpl implements OwnerService {

    private OwnerRepository ownerRepository;
    private TempConverter tempConverter;
    private Provider<VehicleService> vehicleServiceProvider; // 👈 Provider umesto direktnog servisa

    @Inject
    private void initialize(OwnerRepository ownerRepository,
                            TempConverter tempConverter,
                            Provider<VehicleService> vehicleServiceProvider) {
        this.ownerRepository = ownerRepository;
        this.tempConverter = tempConverter;
        this.vehicleServiceProvider = vehicleServiceProvider;
    }

    @Override
    public OwnerDto addOwner(OwnerDto ownerDto) {
        Optional<OwnerEntity> existingOwnerOptional = ownerRepository.findByEmail(ownerDto.getEmail());
        if (existingOwnerOptional.isPresent()) {
            throw new ExistingInstanceException("This email already exists!");
        }

        OwnerEntity storedOwner = ownerRepository.save(tempConverter.ownerDtoToEntity(ownerDto));
        return tempConverter.ownerEntityToDto(storedOwner);

    }

    @Override
    public OwnerDto getOwnerById(Long ownerId) {
        OwnerEntity ownerEntity = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new InstanceUndefinedException("The owner has not been found!"));
        return tempConverter.ownerEntityToDto(ownerEntity);
    }

    @Override
    public OwnerDto updateOwner(OwnerDto ownerDto, Long ownerId) {
        OwnerDto currentOwner = getOwnerById(ownerId);
        Optional<OwnerEntity> existingOwnerOptional = ownerRepository.findByEmail(ownerDto.getEmail());
        if (existingOwnerOptional.isPresent()) {
            OwnerEntity existingOwner = existingOwnerOptional.get();
            if (!Objects.equals(existingOwner.getId(), ownerId)) {
                throw new ExistingInstanceException("This email already exists!");
            }
        }

        ownerDto.setId(currentOwner.getId());
        ownerDto.setVehiclesIds(currentOwner.getVehiclesIds());
        OwnerEntity updatedOwner = ownerRepository.save(tempConverter.ownerDtoToEntity(ownerDto));
        return tempConverter.ownerEntityToDto(updatedOwner);

    }

    @Override
    public void deleteOwner(Long ownerId, String jwtToken) {
        getOwnerById(ownerId);
        
        VehicleService vehicleService = vehicleServiceProvider.get();

        vehicleService.listAllByOwnerId(ownerId)
                .forEach(vehicleDto -> vehicleService.deleteVehicle(vehicleDto.getId(), jwtToken));

        ownerRepository.deleteById(ownerId);
    }

    @Override
    public List<OwnerDto> listAll() {
        return ownerRepository.findAll()
                .stream().map(tempConverter::ownerEntityToDto).collect(Collectors.toList());
    }

    @Override
    public Long count() {
        return ownerRepository.count();
    }
}
