package com.radovan.spring.dto;

import java.io.Serializable;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RadarDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	@NotNull
	@Size(min = 2, max = 100)
	private String name;

	@NotNull
	@Min(0)
	@Max(1)
	private Short status;

	@NotNull
	@Min(30)
	@Max(300)
	private Long maxSpeed;

	@NotNull
	@Min(-180)
	@Max(180)
	private Long longitude;

	@NotNull
	@Min(-90)
	@Max(90)
	private Long latitude;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public Long getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(Long maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public Long getLongitude() {
		return longitude;
	}

	public void setLongitude(Long longitude) {
		this.longitude = longitude;
	}

	public Long getLatitude() {
		return latitude;
	}

	public void setLatitude(Long latitude) {
		this.latitude = latitude;
	}

}
