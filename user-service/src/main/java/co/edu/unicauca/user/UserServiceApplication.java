package co.edu.unicauca.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * User Service (:8081). Autentica usuarios y verifica sus roles.
 * Comunicación: REST síncrono (POST /auth/login, GET /auth/validate, GET /users/{id}).
 */
@SpringBootApplication
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
