package com.radovan.spring.services;

import java.util.List;

import com.radovan.spring.dto.RadarDto;

public interface RadarService {

	RadarDto addRadar(RadarDto radarDto);

	RadarDto getRadarById(Long radarId);

	RadarDto updateRadar(RadarDto radarDto, Long radarId);

	void deleteRadar(Long radarId);

	List<RadarDto> listAll();

	List<RadarDto> listAllByName(String keyword);

	List<RadarDto> listAllActive();

	void activateRadarPatrol();

	void deactivateRadarPatrol();

	void activateRadar(Long radarId);

	void deactivateRadar(Long radarId);
}
