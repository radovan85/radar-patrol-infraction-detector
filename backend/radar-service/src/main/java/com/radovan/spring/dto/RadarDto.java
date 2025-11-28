package com.radovan.spring.dto;

import java.io.Serializable;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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
	private Integer maxSpeed;

	@NotNull
	@DecimalMin(value = "-180")
	@DecimalMax(value = "180")
	private Double longitude; // geografska dužina

	@NotNull
	@DecimalMin(value = "-90")
	@DecimalMax(value = "90")
	private Double latitude; // geografska širina

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

	public Integer getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(Integer maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

}
