package ru.itgirl.libraryproject.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.itgirl.libraryproject.model.AppUser;
import ru.itgirl.libraryproject.repository.AppUserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Обозначаем сервис для бизнес-логики
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final AppUserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Метод загрузки пользователя по имени для проверки аутентификации
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Ищем пользователя в базе
        log.info("Search user by username {}", username);
        Optional<AppUser> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            AppUser appUser = user.get();
            // Разбиваем строку ролей на отдельные элементы и оборачиваем их в SimpleGrantedAuthority
            List<SimpleGrantedAuthority> authorities = Arrays.stream(appUser.getRoles().split(","))
                    .map(String::trim)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            // Формируем объект User (из Spring Security) для дальнейшей проверки
            UserDetails userDetails = new User(
                    appUser.getUsername(), appUser.getPassword(), appUser.isEnabled(),
                    true, // accountNonExpired: учетная запись не просрочена
                    true, // credentialsNonExpired: пароль не просрочен
                    true, // accountNonLocked: учетная запись не заблокирована
                    authorities);
            log.info("User found with username {}", username);
            return userDetails;
        } else {
            log.error("User not found with username {}", username);
            throw new UsernameNotFoundException("Error searching user");
        }
    }
}