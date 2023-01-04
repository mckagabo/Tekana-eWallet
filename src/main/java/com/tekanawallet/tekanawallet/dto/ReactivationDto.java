package com.tekanawallet.tekanawallet.dto;

import jakarta.validation.constraints.Email;

public class ReactivationDto {
	@Email
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	

}
