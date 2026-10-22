package co.edu.unicauca.user.dto;

import co.edu.unicauca.user.model.User;

import java.util.List;

public record UserDTO(Long id, String username, String fullName, String email, List<String> roles) {

    public static UserDTO from(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getFullName(), user.getEmail(),
                user.getRoles().stream().map(Enum::name).sorted().toList());
    }
}
