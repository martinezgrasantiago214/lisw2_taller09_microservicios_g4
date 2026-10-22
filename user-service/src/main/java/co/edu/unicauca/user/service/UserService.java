package co.edu.unicauca.user.service;

import co.edu.unicauca.user.dto.LoginRequestDTO;
import co.edu.unicauca.user.dto.LoginResponseDTO;
import co.edu.unicauca.user.dto.UserDTO;
import co.edu.unicauca.user.exception.NotFoundException;
import co.edu.unicauca.user.exception.UnauthorizedException;
import co.edu.unicauca.user.model.Role;
import co.edu.unicauca.user.model.User;
import co.edu.unicauca.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final String BEARER = "Bearer ";

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    /** Sesiones activas: token opaco -> id de usuario. */
    private final Map<String, Long> sessions = new ConcurrentHashMap<>();

    public UserService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByUsername(request.username())
                .filter(u -> u.validatePassword(passwordHasher.hash(u.getUsername(), request.password())))
                .orElseThrow(() -> new UnauthorizedException("Usuario o contraseña incorrectos"));
        String token = UUID.randomUUID().toString();
        sessions.put(token, user.getId());
        log.info("[UserService] Login exitoso de '{}' con roles {}", user.getUsername(), user.getRoles());
        return LoginResponseDTO.success(token, UserDTO.from(user));
    }

    @Transactional(readOnly = true)
    public UserDTO validateToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER)) {
            throw new UnauthorizedException("Falta el encabezado Authorization: Bearer <token>");
        }
        String token = authorizationHeader.substring(BEARER.length()).trim();
        Long userId = sessions.get(token);
        if (userId == null) {
            throw new UnauthorizedException("Token inválido o sesión cerrada");
        }
        return userRepository.findById(userId).map(UserDTO::from)
                .orElseThrow(() -> new UnauthorizedException("El usuario de la sesión ya no existe"));
    }

    public void logout(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            sessions.remove(authorizationHeader.substring(BEARER.length()).trim());
        }
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        return userRepository.findById(id).map(UserDTO::from)
                .orElseThrow(() -> new NotFoundException("No existe el usuario con id " + id));
    }

    @Transactional(readOnly = true)
    public List<UserDTO> findAll(Role role) {
        List<User> users = role == null ? userRepository.findAll() : userRepository.findByRolesContaining(role);
        return users.stream().map(UserDTO::from).toList();
    }
}
