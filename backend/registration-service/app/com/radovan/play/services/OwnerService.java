package com.radovan.play.services;

import com.radovan.play.dto.OwnerDto;

import java.util.List;

public interface OwnerService {

    OwnerDto addOwner(OwnerDto ownerDto);

    OwnerDto getOwnerById(Long ownerId);

    OwnerDto updateOwner(OwnerDto ownerDto,Long ownerId);

    void deleteOwner(Long ownerId,String jwtToken);

    List<OwnerDto> listAll();

    Long count();
}
