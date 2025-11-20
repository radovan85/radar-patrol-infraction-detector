package com.radovan.spring.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radovan.spring.dto.RadarDto;
import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.services.RadarService;

@RestController
@RequestMapping(value = "/api/radars")
public class RadarController {

	@Autowired
	private RadarService radarService;

	@GetMapping
	public ResponseEntity<List<RadarDto>> getAllRadars() {
		return new ResponseEntity<>(radarService.listAll(), HttpStatus.OK);
	}

	@GetMapping(value = "/search/{keyword}")
	public ResponseEntity<List<RadarDto>> getAllByKeyword(@PathVariable("keyword") String keyword) {
		return new ResponseEntity<>(radarService.listAllByName(keyword), HttpStatus.OK);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<RadarDto> getRadar(@PathVariable("id") Long radarId) {
		return new ResponseEntity<>(radarService.getRadarById(radarId), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<String> saveRadar(@RequestBody @Validated RadarDto radarDto, Errors errors) {
		if (errors.hasErrors()) {
			throw new DataNotValidatedException("Radar data has not been validated!");
		}

		RadarDto storedRadar = radarService.addRadar(radarDto);
		return new ResponseEntity<>("Radar with id " + storedRadar.getId() + " has been stored!", HttpStatus.CREATED);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<String> updateRadar(@RequestBody @Validated RadarDto radarDto,
			@PathVariable("id") Long radarId, Errors errors) {
		if (errors.hasErrors()) {
			throw new DataNotValidatedException("Radar data has not been validated!");
		}

		RadarDto updatedRadar = radarService.updateRadar(radarDto, radarId);
		return new ResponseEntity<>("Radar with id " + updatedRadar.getId() + " has been updated!", HttpStatus.OK);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<String> deleteRadar(@PathVariable("id") Long radarId) {
		radarService.deleteRadar(radarId);
		return new ResponseEntity<>("Radar with id " + radarId + " has been permanently removed!", HttpStatus.OK);
	}

	@GetMapping(value = "/activatePatrol")
	public ResponseEntity<String> activatePatrol() {
		radarService.activateRadarPatrol();
		return new ResponseEntity<>("Radar control has been activated!", HttpStatus.OK);
	}
	
	@GetMapping(value="/deactivatePatrol")
	public ResponseEntity<String> deactivatePatrol(){
		radarService.deactivateRadarPatrol();
		return new ResponseEntity<>("Radar control has been terminated!", HttpStatus.OK);
	}

}
