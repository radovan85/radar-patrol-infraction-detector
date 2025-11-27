package com.radovan.spring.converter;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.radovan.spring.dto.RadarDto;
import com.radovan.spring.entity.RadarEntity;

@Component
public class TempConverter {

	private ModelMapper mapper;

	@Autowired
	private void initialize(ModelMapper mapper) {
		this.mapper = mapper;
	}

	public RadarDto entityToDto(RadarEntity radarEntity) {
		RadarDto returnValue = mapper.map(radarEntity, RadarDto.class);
		Optional<Byte> statusOptional = Optional.ofNullable(radarEntity.getStatus());
		statusOptional.ifPresent(status -> returnValue.setStatus(status.shortValue()));
		return returnValue;
	}

	public RadarEntity dtoToEntity(RadarDto radarDto) {
		RadarEntity returnValue = mapper.map(radarDto, RadarEntity.class);
		Optional<Short> statusOptional = Optional.ofNullable(radarDto.getStatus());
		statusOptional.ifPresent(status -> returnValue.setStatus(status.byteValue()));
		return returnValue;
	}

}
