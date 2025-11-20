package com.radovan.play.converter;

import com.radovan.play.dto.OwnerDto;
import com.radovan.play.dto.VehicleDto;
import com.radovan.play.entity.OwnerEntity;
import com.radovan.play.entity.VehicleEntity;
import com.radovan.play.repositories.OwnerRepository;
import com.radovan.play.repositories.VehicleRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Singleton
public class TempConverter {

    private ModelMapper mapper;
    private OwnerRepository ownerRepository;
    private VehicleRepository vehicleRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Inject
    private void initialize(ModelMapper mapper, OwnerRepository ownerRepository, VehicleRepository vehicleRepository) {
        this.mapper = mapper;
        this.ownerRepository = ownerRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public OwnerDto ownerEntityToDto(OwnerEntity ownerEntity){
        OwnerDto returnValue = mapper.map(ownerEntity,OwnerDto.class);

        Optional<LocalDate> birthDateOptional = Optional.ofNullable(ownerEntity.getBirthDate());
        birthDateOptional.ifPresent(birthDate -> {
            // LocalDate → String
            returnValue.setBirthDateStr(birthDate.format(DATE_FORMATTER));
        });

        Optional<List<VehicleEntity>> vehiclesOptional = Optional.ofNullable(ownerEntity.getVehicles());
        List<Long> vehiclesIds = new ArrayList<>();
        vehiclesOptional.ifPresent(vehicles -> {
            vehicles.forEach(vehicleEntity -> vehiclesIds.add(vehicleEntity.getId()));
        });
        returnValue.setVehiclesIds(vehiclesIds);

        return returnValue;
    }

    public OwnerEntity ownerDtoToEntity(OwnerDto ownerDto){
        OwnerEntity returnValue = mapper.map(ownerDto,OwnerEntity.class);

        Optional<String> birthDateStrOptional = Optional.ofNullable(ownerDto.getBirthDateStr());
        birthDateStrOptional.ifPresent(birthDateStr -> {
            try {
                // String → LocalDate
                LocalDate parsedDate = LocalDate.parse(birthDateStr, DATE_FORMATTER);
                returnValue.setBirthDate(parsedDate);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format, expected yyyy-MM-dd: " + birthDateStr);
            }
        });

        Optional<List<Long>> vehiclesIdsOptional = Optional.ofNullable(ownerDto.getVehiclesIds());
        List<VehicleEntity> vehicles = new ArrayList<>();
        vehiclesIdsOptional.ifPresent(vehiclesIds -> {
            vehiclesIds.forEach(vehicleId -> {
                vehicleRepository.findById(vehicleId).ifPresent(vehicles::add);
            });
        });

        returnValue.setVehicles(vehicles);
        return returnValue;
    }

    public VehicleDto vehicleEntityToDto(VehicleEntity vehicleEntity){
        VehicleDto returnValue = mapper.map(vehicleEntity,VehicleDto.class);
        Optional<OwnerEntity> ownerOptional = Optional.ofNullable(vehicleEntity.getOwner());
        ownerOptional.ifPresent(ownerEntity -> returnValue.setOwnerId(ownerEntity.getId()));
        return returnValue;
    }

    public VehicleEntity vehicleDtoToEntity(VehicleDto vehicleDto){
        VehicleEntity returnValue = mapper.map(vehicleDto, VehicleEntity.class);
        Optional<Long> ownerIdOptional = Optional.ofNullable(vehicleDto.getOwnerId());
        ownerIdOptional.flatMap(ownerId -> ownerRepository.findById(ownerId)).ifPresent(returnValue::setOwner);
        return returnValue;
    }
}
