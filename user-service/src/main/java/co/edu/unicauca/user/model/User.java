package co.edu.unicauca.user.model;

import jakarta.persistence.*;

import java.util.EnumSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    private String fullName;

    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles = EnumSet.noneOf(Role.class);

    protected User() {
    }

    public User(String username, String passwordHash, String fullName, String email, Set<Role> roles) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.roles = roles;
    }

    public boolean validatePassword(String candidateHash) {
        return passwordHash.equals(candidateHash);
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public Set<Role> getRoles() { return roles; }
}
