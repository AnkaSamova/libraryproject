package ru.itgirl.libraryproject.service.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.itgirl.libraryproject.dto.AuthorDto;
import ru.itgirl.libraryproject.dto.BookCreateDto;
import ru.itgirl.libraryproject.dto.BookDto;
import ru.itgirl.libraryproject.dto.BookUpdateDto;
import ru.itgirl.libraryproject.model.Author;
import ru.itgirl.libraryproject.model.Book;
import ru.itgirl.libraryproject.repository.AuthorRepository;
import ru.itgirl.libraryproject.repository.BookRepository;
import ru.itgirl.libraryproject.repository.GenreRepository;
import ru.itgirl.libraryproject.service.BookService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;

    @Override
    public BookDto getByNameV1(String name) {
        log.info("Try to find book by name {}", name);
        Optional<Book> book = bookRepository.findBookByName(name);
        if (book.isPresent()) {
            BookDto bookDto = convertEntityToDto(book.get());
            log.info("Book: {}", bookDto.toString());
            return bookDto;
        } else {
            log.error("Book with name {} not found", name);
            throw new NoSuchElementException("No value present");
        }
    }

    @Override
    public BookDto getByNameV2(String name) {
        log.info("Try to find book by name {}", name);
        Optional<Book> book = bookRepository.findBookByNameBySql(name);
        if (book.isPresent()) {
            BookDto bookDto = convertEntityToDto(book.get());
            log.info("Book: {}", bookDto.toString());
            return bookDto;
        } else {
            log.error("Book with name {} not found", name);
            throw new NoSuchElementException("No value present");
        }
    }

    @Override
    public BookDto getByNameV3(String name) {
        Specification<Book> specification = Specification.where(new Specification<Book>() {
            @Override
            public Predicate toPredicate(Root<Book> root,
                                         CriteriaQuery<?> query,
                                         CriteriaBuilder cb) {
                return cb.equal(root.get("name"), name);
            }
        });
        log.info("Try to find book by name {}", name);
        Optional<Book> book = bookRepository.findOne(specification);
        if (book.isPresent()) {
            BookDto bookDto = convertEntityToDto(book.get());
            log.info("Book: {}", bookDto.toString());
            return bookDto;
        } else {
            log.error("Book with name {} not found", name);
            throw new NoSuchElementException("No value present");
        }
    }

    @Override
    public BookDto createBook(BookCreateDto bookCreateDto) {
        log.info("Try to create new book: {}", bookCreateDto.getName());
        Optional<Book> book = Optional.of(bookRepository.save(convertDtoToEntity(bookCreateDto)));
        if (book.isPresent()) {
            BookDto bookDto = convertEntityToDto(book.get());
            log.info("New book created: {}", bookDto.toString());
            return bookDto;
        } else {
            log.error("New book not created");
            throw new RuntimeException("Error creating new book");
        }
    }

    @Override
    public BookDto updateBook(BookUpdateDto bookUpdateDto) {
        log.info("Try to update book with id {}", bookUpdateDto.getId());
        Optional<Book> book = bookRepository.findById(bookUpdateDto.getId());
        if (book.isPresent()) {
            Book bookEntity = book.get();
            bookEntity.setName(bookUpdateDto.getName());
            bookEntity.setGenre(genreRepository.findById(bookUpdateDto.getGenreId()).orElseThrow());
            bookEntity.setAuthors(bookUpdateDto.getAuthorId()
                    .stream()
                    .map(id -> authorRepository.findById(id).orElseThrow())
                    .collect(Collectors.toSet()));
            Book savedBook = bookRepository.save(bookEntity);
            BookDto bookDto = convertEntityToDto(savedBook);
            log.info("Book with id {} updated: {}", bookDto.getId(), bookDto);
            return bookDto;
        } else {
            log.error("Book with id {} not found", bookUpdateDto.getId());
            throw new NoSuchElementException("No value present");
        }
    }

    @Override
    public void deleteBook(Long id) {
        log.info("Try to delete book by id {}", id);
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            bookRepository.deleteById(id);
            log.info("Book with id {} deleted", id);
        } else {
            log.error("Book with id {} not found", id);
            throw new NoSuchElementException("No value present");
        }
    }

    @Override
    public List<BookDto> getAllBooks() {
        log.info("Try to get all books");
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) {
            log.error("Books not found");
            throw new RuntimeException("Error getting all books");
        } else {
            List<BookDto> bookDto = books.stream().map(this::convertEntityToDto).collect(Collectors.toList());
            log.info("Books found: {}", bookDto);
            return bookDto;
        }
    }

    private Book convertDtoToEntity(BookCreateDto bookCreateDto) {
        Set<Author> author = bookCreateDto.getAuthorId()
                .stream()
                .map(id -> authorRepository.findById(id).orElseThrow())
                .collect(Collectors.toSet());
        return Book.builder()
                .name(bookCreateDto.getName())
                .genre(genreRepository.findById(bookCreateDto.getGenreId()).orElseThrow())
                .authors(author)
                .build();
    }

    private BookDto convertEntityToDto(Book book) {
        List<AuthorDto> authorDtoList = null;
        if (book.getAuthors() != null) {
            authorDtoList = book.getAuthors()
                    .stream()
                    .map(author -> AuthorDto.builder()
                            .id(author.getId())
                            .name(author.getName())
                            .surname(author.getSurname())
                            .build())
                    .toList();
        }
        return BookDto.builder()
                .id(book.getId())
                .name(book.getName())
                .genre(book.getGenre().getName())
                .authors(authorDtoList)
                .build();
    }
}