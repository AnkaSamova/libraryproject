package ru.itgirl.libraryproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookUpdateDto {
    @NotNull(message = "Необходимо указать id")
    private Long id;
    @NotBlank(message = "Необходимо указать название")
    private String name;
    @NotNull(message = "Необходимо указать id жанра")
    private Long genreId;
    @Size(min = 1)
    private Set<Long> authorId;
}