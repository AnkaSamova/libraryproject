package ru.itgirl.libraryproject.service.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.itgirl.libraryproject.dto.AuthorCreateDto;
import ru.itgirl.libraryproject.dto.AuthorDto;
import ru.itgirl.libraryproject.dto.AuthorUpdateDto;
import ru.itgirl.libraryproject.dto.BookDto;
import ru.itgirl.libraryproject.model.Author;
import ru.itgirl.libraryproject.repository.AuthorRepository;
import ru.itgirl.libraryproject.service.AuthorService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    @Override
    public AuthorDto getAuthorById(Long id) {
        log.info("Try to find author by id {}", id);
        Optional<Author> author = authorRepository.findById(id);
        if (author.isPresent()) {
            AuthorDto authorDto = convertEntityToDto(author.get());
            log.info("Author: {}", authorDto.toString());
            return authorDto;
        } else {
            log.error("Author with id {} not found", id);
            throw new NoSuchElementException("No value present");
        }
    }

    @Override
    public AuthorDto getByNameV1(String name) {
        log.info("Try to find author by name {}", name);
        Optional<Author> author = authorRepository.findAuthorByName(name);
        if (author.isPresent()) {
            AuthorDto authorDto = convertToDto(author.get());
            log.info("Author: {}", authorDto.toString());
            return authorDto;
        } else {
            log.error("Author with name {} not found", name);
            throw new NoSuchElementException("No value present");
        }
    }

    @Override
    public AuthorDto getByNameV2(String name) {
        log.info("Try to find author by name {}", name);
        Optional<Author> author = authorRepository.findAuthorByNameBySql(name);
        if (author.isPresent()) {
            AuthorDto authorDto = convertToDto(author.get());
            log.info("Author: {}", authorDto.toString());
            return authorDto;
        } else {
            log.error("Author with name {} not found", name);
            throw new NoSuchElementException("No value present");
        }
    }

    @Override
    public AuthorDto getByNameV3(String name) {
        Specification<Author> specification = Specification.where(new Specification<Author>() {
            @Override
            public Predicate toPredicate(Root<Author> root,
                                         CriteriaQuery<?> query,
                                         CriteriaBuilder cb) {
                return cb.equal(root.get("name"), name);
            }
        });
        log.info("Try to find author by name {}", name);
        Optional<Author> author = authorRepository.findOne(specification);
        if (author.isPresent()) {
            AuthorDto authorDto = convertToDto(author.get());
            log.info("Author: {}", authorDto.toString());
            return authorDto;
        } else {
            log.error("Author with name {} not found", name);
            throw new NoSuchElementException("No value present");
        }
    }

    @Override
    public AuthorDto createAuthor(AuthorCreateDto authorCreateDto) {
        log.info("Try to create new author: {}", authorCreateDto.toString());
        Optional<Author> author = Optional.of(authorRepository.save(convertDtoToEntity(authorCreateDto)));
        if (author.isPresent()) {
            AuthorDto authorDto = convertEntityToDto(author.get());
            log.info("New author created: {}", authorDto.toString());
            return authorDto;
        } else {
            log.error("New author not created");
            throw new RuntimeException("Error creating new author");
        }
    }

    @Override
    public AuthorDto updateAuthor(AuthorUpdateDto authorUpdateDto) {
        log.info("Try to update author with id {}", authorUpdateDto.getId());
        Optional<Author> author = authorRepository.findById(authorUpdateDto.getId());
        if (author.isPresent()) {
            Author authorEntity = author.get();
            authorEntity.setName(authorUpdateDto.getName());
            authorEntity.setSurname(authorUpdateDto.getSurname());
            Author savedAuthor = authorRepository.save(authorEntity);
            AuthorDto authorDto = convertEntityToDto(savedAuthor);
            log.info("Author with id {} updated: {}", authorDto.getId(), authorDto);
            return authorDto;
        } else {
            log.error("Author with id {} not found", authorUpdateDto.getId());
            throw new NoSuchElementException("No value present");
        }
    }

    @Override
    public void deleteAuthor(Long id) {
        log.info("Try to delete author by id {}", id);
        Optional<Author> author = authorRepository.findById(id);
        if (author.isPresent()) {
            authorRepository.deleteById(id);
            log.info("Author with id {} deleted", id);
        } else {
            log.error("Author with id {} not found", id);
            throw new NoSuchElementException("No value present");
        }
    }

    @Override
    public List<AuthorDto> getAllAuthors() {
        log.info("Try to get all authors");
        List<Author> authors = authorRepository.findAll();
        if (authors.isEmpty()) {
            log.error("Authors not found");
            throw new RuntimeException("Error getting all authors");
        } else {
            List<AuthorDto> authorsDto = authors.stream().map(this::convertEntityToDto).collect(Collectors.toList());
            log.info("Authors found: {}", authorsDto);
            return authorsDto;
        }
    }

    private AuthorDto convertToDto(Author author) {
        List<BookDto> bookDtoList = author.getBooks()
                .stream()
                .map(book -> BookDto.builder()
                        .genre(book.getGenre().getName())
                        .name(book.getName())
                        .id(book.getId())
                        .build()
                ).toList();
        return AuthorDto.builder()
                .books(bookDtoList)
                .id(author.getId())
                .name(author.getName())
                .surname(author.getSurname())
                .build();
    }

    private Author convertDtoToEntity(AuthorCreateDto authorCreateDto) {
        return Author.builder()
                .name(authorCreateDto.getName())
                .surname(authorCreateDto.getSurname())
                .build();
    }

    private AuthorDto convertEntityToDto(Author author) {
        List<BookDto> bookDtoList = null;
        if (author.getBooks() != null) {
            bookDtoList = author.getBooks()
                    .stream()
                    .map(book -> BookDto.builder()
                            .genre(book.getGenre().getName())
                            .name(book.getName())
                            .id(book.getId())
                            .build())
                    .toList();
        }
        return AuthorDto.builder()
                .id(author.getId())
                .name(author.getName())
                .surname(author.getSurname())
                .books(bookDtoList)
                .build();
    }
}