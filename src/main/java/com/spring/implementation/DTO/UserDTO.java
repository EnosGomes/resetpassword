package com.spring.implementation.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {

	@Size(min = 2, message = "Name should have at least 2 characters")
	@NotEmpty(message = "Name should not be empty")
	private String name;
	private String email;
	private String password;
	private String apellido;
}
