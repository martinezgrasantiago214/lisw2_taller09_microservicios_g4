package co.edu.unicauca.user.controller;

import co.edu.unicauca.user.dto.UserDTO;
import co.edu.unicauca.user.model.Role;
import co.edu.unicauca.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> findAll(@RequestParam(required = false) Role role) {
        return ResponseEntity.ok(userService.findAll(role));
    }
}
