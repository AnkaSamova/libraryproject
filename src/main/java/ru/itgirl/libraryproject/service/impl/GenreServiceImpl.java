package ru.itgirl.libraryproject.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itgirl.libraryproject.dto.AuthorDto;
import ru.itgirl.libraryproject.dto.BookDto;
import ru.itgirl.libraryproject.dto.GenreDto;
import ru.itgirl.libraryproject.model.Genre;
import ru.itgirl.libraryproject.repository.GenreRepository;
import ru.itgirl.libraryproject.service.GenreService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public GenreDto getGenreById(Long id) {
        Genre genre = genreRepository.findById(id).orElseThrow();
        return convertToDto(genre);
    }

    private GenreDto convertToDto(Genre genre) {
        GenreDto genreDto = new GenreDto();
        genreDto.setId(genre.getId());
        genreDto.setGenre(genre.getName());
        List<BookDto> bookDtoList = genre.getBooks()
                .stream()
                .map(book -> {
                    BookDto bookDto = new BookDto();
                    bookDto.setId(book.getId());
                    bookDto.setName(book.getName());
                    bookDto.setAuthors(book.getAuthors().stream()
                            .map(author -> {
                                AuthorDto authorDto = new AuthorDto();
                                authorDto.setId(author.getId());
                                authorDto.setName(author.getName());
                                authorDto.setSurname(author.getSurname());
                                return authorDto;
                            }).collect(Collectors.toList()));
                    return bookDto;
                }).toList();
        genreDto.setBooks(bookDtoList);
        return genreDto;
    }
}