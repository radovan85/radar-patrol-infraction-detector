package com.radovan.play.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import play.data.validation.Constraints;

import java.io.Serializable;
import java.util.List;

public class OwnerDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @Constraints.Required
    @Constraints.MinLength(value = 5)
    @Constraints.MaxLength(value = 60)
    private String name;

    @Constraints.Required
    private String birthDateStr;

    @Constraints.Email
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Long> vehiclesIds;

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

    public String getBirthDateStr() {
        return birthDateStr;
    }

    public void setBirthDateStr(String birthDateStr) {
        this.birthDateStr = birthDateStr;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Long> getVehiclesIds() {
        return vehiclesIds;
    }

    public void setVehiclesIds(List<Long> vehiclesIds) {
        this.vehiclesIds = vehiclesIds;
    }

    public OwnerDto(String name, String birthDateStr, String email) {
        this.name = name;
        this.birthDateStr = birthDateStr;
        this.email = email;
    }

    public OwnerDto() {
    }
}
