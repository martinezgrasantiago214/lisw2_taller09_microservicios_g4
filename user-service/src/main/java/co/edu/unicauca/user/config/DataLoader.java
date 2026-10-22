package co.edu.unicauca.user.config;

import co.edu.unicauca.user.model.Role;
import co.edu.unicauca.user.model.User;
import co.edu.unicauca.user.repository.UserRepository;
import co.edu.unicauca.user.service.PasswordHasher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumSet;

/** Usuarios de prueba (contraseña = "<usuario>123"). */
@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner seedUsers(UserRepository repository, PasswordHasher hasher) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            save(repository, hasher, "admin", "Administrador del Banco", "admin@unicauca.edu.co",
                    EnumSet.of(Role.ADMIN));
            save(repository, hasher, "docente1", "Docente Autor Uno", "docente1@unicauca.edu.co",
                    EnumSet.of(Role.TEACHER, Role.AUTHOR));
            save(repository, hasher, "revisor1", "Revisor Par Uno", "revisor1@unicauca.edu.co",
                    EnumSet.of(Role.TEACHER, Role.REVIEWER));
            save(repository, hasher, "revisor2", "Revisor Par Dos", "revisor2@unicauca.edu.co",
                    EnumSet.of(Role.REVIEWER));
            save(repository, hasher, "estudiante1", "Estudiante Uno", "estudiante1@unicauca.edu.co",
                    EnumSet.of(Role.STUDENT));
        };
    }

    private void save(UserRepository repository, PasswordHasher hasher, String username, String fullName,
                      String email, EnumSet<Role> roles) {
        repository.save(new User(username, hasher.hash(username, username + "123"), fullName, email, roles));
    }
}
