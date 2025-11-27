package com.radovan.spring.dto;

import java.io.Serializable;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class RoleDto implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	@NotEmpty
	@Size(max = 30)
	private String role;

	private List<Long> usersIds;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<Long> getUsersIds() {
		return usersIds;
	}

	public void setUsersIds(List<Long> usersIds) {
		this.usersIds = usersIds;
	}

}
