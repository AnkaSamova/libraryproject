package ru.itgirl.libraryproject.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AuthorUpdateDto {
    @NotNull(message = "Необходимо указать id")
    private Long id;
    @Size(min = 3, max = 10)
    private String name;
    @Size(min = 1, max = 20)
    private String surname;
}