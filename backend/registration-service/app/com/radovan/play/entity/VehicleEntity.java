package com.radovan.play.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name="vehicles")
public class VehicleEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true,name="registration_number",length = 25)
    private String registrationNumber;

    @Column(nullable = false,length = 30)
    private String brand;

    @Column(nullable = false,name = "fiscal_power")
    private Double fiscalPower;

    @Column(nullable = false,length = 30)
    private String model;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id",nullable = false)
    private OwnerEntity owner;

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

    public OwnerEntity getOwner() {
        return owner;
    }

    public void setOwner(OwnerEntity owner) {
        this.owner = owner;
    }


}
