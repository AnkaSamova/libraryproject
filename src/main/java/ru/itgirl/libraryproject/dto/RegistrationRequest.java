package ru.itgirl.libraryproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO (Data Transfer Object) для регистрации нового пользователя
public class RegistrationRequest {
    @Size(min = 3, max = 20)
    @NotBlank(message = "Необходимо указать имя")
    private String username;
    @Size(min = 8)
    @NotBlank(message = "Необходимо указать пароль")
    private String password;

    // Геттеры и сеттеры
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
}