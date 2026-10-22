package co.edu.unicauca.question.client;

import java.util.List;

/** Usuario devuelto por el User Service (GET /auth/validate o GET /users/{id}). */
public record AuthenticatedUser(Long id, String username, String fullName, String email, List<String> roles) {

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    public boolean hasAnyRole(String... candidates) {
        for (String role : candidates) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }
}
