package com.radovan.spring.services;

import java.util.List;

import com.radovan.spring.dto.RadarDto;

public interface RadarService {

	RadarDto addRadar(RadarDto radarDto);

	RadarDto getRadarById(Long radarId);

	RadarDto updateRadar(RadarDto radarDto, Long radarId);

	void deleteRadar(Long radarId,String jwtToken);

	List<RadarDto> listAll();

	List<RadarDto> listAllByName(String keyword);

	List<RadarDto> listAllAvailable();

	void deactivateRadarPatrol();

	void turnOnAvailability(Long radarId);

	void turnOffAvailability(Long radarId);

	void activateRadarPatrol(String jwtToken);
}
