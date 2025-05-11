package ru.itgirl.libraryproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // Включает авто-конфигурацию, сканирование компонентов и т.д.
public class LibraryprojectApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryprojectApplication.class, args);
    }
}