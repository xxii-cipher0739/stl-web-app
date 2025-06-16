package xyz.playground.stl_web_app.Model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles = new HashSet<>();

    private boolean enabled = true;

    public User() {
    }

    public User(String name, String username, String password, Set<String> roles) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean hasRole(String role) {
        return this.roles.contains(role);
    }

    public String getPrimaryRole() {
        if (this.roles.contains("ADMIN")) {
            return "ADMIN";
        } else if (this.roles.contains("DISPATCHER")) {
            return "DISPATCHER";
        } else if (this.roles.contains("COLLECTOR")) {
            return "COLLECTOR";
        }
        return "USER";
    }
}
