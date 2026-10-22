package co.edu.unicauca.user.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "El nombre de usuario es obligatorio") String username,
        @NotBlank(message = "La contraseña es obligatoria") String password) {
}
