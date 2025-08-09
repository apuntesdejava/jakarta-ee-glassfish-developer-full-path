package com.tuempresa.proyecto.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "APP_USERS") // Para diferenciarla de la tabla USERS del realm si es necesario
public class User extends BaseEntity {

    @NotBlank(message = "El nombre de usuario no puede estar vacío.")
    @Size(max = 50, message = "El nombre de usuario no puede exceder 50 caracteres.")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    private String password; // NOTA: En un sistema real, NUNCA almacenar contraseñas en texto plano. Usar hashing.

    @NotBlank(message = "El correo electrónico no puede estar vacío.")
    @Email(message = "Formato de correo electrónico inválido.")
    private String email;

    @Size(max = 100, message = "El nombre completo no puede exceder 100 caracteres.")
    private String fullName;

    @OneToMany(mappedBy = "assignedUser", fetch = FetchType.LAZY)
    private Set<Task> assignedTasks = new HashSet<>();

    public User() {
    }

    public User(String username, String password, String email, String fullName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
    }

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // ... getters y setters...
    public Set<Task> getAssignedTasks() {
        return assignedTasks;
    }

    public void setAssignedTasks(Set<Task> assignedTasks) {
        this.assignedTasks = assignedTasks;
    }

    @Override
    public String toString() {
        return "User{"
                + "id=" + id
                + ", username='" + username + '\''
                + ", email='" + email + '\''
                + ", fullName='" + fullName + '\''
                + '}';
    }
}
