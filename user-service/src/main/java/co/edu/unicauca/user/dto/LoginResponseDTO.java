package co.edu.unicauca.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Respuesta del login. El token es un identificador de sesión opaco (UUID) que
 * los demás servicios validan contra GET /auth/validate. No es un JWT: los
 * tokens firmados se implementan en el corte 3.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponseDTO(boolean authenticated, String token, UserDTO user, String message) {

    public static LoginResponseDTO success(String token, UserDTO user) {
        return new LoginResponseDTO(true, token, user, "Autenticación exitosa");
    }

    public static LoginResponseDTO failure(String message) {
        return new LoginResponseDTO(false, null, null, message);
    }
}
