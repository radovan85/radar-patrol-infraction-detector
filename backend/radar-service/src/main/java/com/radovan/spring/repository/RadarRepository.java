package com.radovan.spring.repository;

import java.util.List;
import java.util.Optional;

import com.radovan.spring.entity.RadarEntity;

public interface RadarRepository {

	List<RadarEntity> findAllByName(String keyword);

	RadarEntity save(RadarEntity radarEntity);

	Optional<RadarEntity> findById(Long radarId);

	void deleteById(Long radarId);

	List<RadarEntity> findAll();
	
	List<RadarEntity> findAllActive();

}
