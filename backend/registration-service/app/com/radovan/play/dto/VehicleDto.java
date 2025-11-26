package com.radovan.play.dto;

import com.radovan.play.validation.YearNotInFuture;
import play.data.validation.Constraints;

import java.io.Serializable;

public class VehicleDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @Constraints.Required
    @Constraints.MinLength(value = 5)
    @Constraints.MaxLength(value = 25)
    private String registrationNumber;

    @Constraints.Required
    @Constraints.MinLength(value = 2)
    @Constraints.MaxLength(value = 30)
    private String brand;

    @Constraints.Required
    @Constraints.Min(value = 1)
    @Constraints.Max(value = 30)
    private Double fiscalPower;

    @Constraints.Required
    @Constraints.MinLength(value = 2)
    @Constraints.MaxLength(value = 30)
    private String model;

    @Constraints.Required
    @Constraints.Min(value = 1960)
    @YearNotInFuture
    private Integer manufactureYear;

    @Constraints.Required
    @Constraints.Min(value = 1)
    private Long ownerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Double getFiscalPower() {
        return fiscalPower;
    }

    public void setFiscalPower(Double fiscalPower) {
        this.fiscalPower = fiscalPower;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }


    public Integer getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(Integer manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    public VehicleDto() {
    }

    public VehicleDto(String registrationNumber, String brand, Double fiscalPower, String model, Integer manufactureYear) {
        this.registrationNumber = registrationNumber;
        this.brand = brand;
        this.fiscalPower = fiscalPower;
        this.model = model;
        this.manufactureYear = manufactureYear;
    }
}
