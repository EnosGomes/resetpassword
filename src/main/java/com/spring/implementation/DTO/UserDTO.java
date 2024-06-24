package com.spring.implementation.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {

	@Size(min = 3, message = "El nombre debe tener al menos 3 caracteres.")
	@NotEmpty(message = "El nombre no debe estar vacío.")
	private String name;

	@Email( message = "Email inválido.")
	@NotEmpty(message = "El email no debe estar vacío.")
	private String email;

	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$@!%&*?])[A-Za-z\\d#$@!%&*?]{8,}$", message = "La contraseña debe tener al menos 8 caracteres,un símbolo, una letra mayúscula, una letra minúscula y un número.")
	private String password;

	@Size(min = 2, message = "El apellido debe tener al menos 2 caracteres.")
	@NotEmpty(message = "El apellido no debe estar vacío.")
	private String apellido;
}
