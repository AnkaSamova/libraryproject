package ru.itgirl.libraryproject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import ru.itgirl.libraryproject.service.impl.CustomUserDetailsService;

@Configuration
@EnableWebSecurity // Включает возможности Spring Security для веб-приложения
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // Настройка цепочки фильтров безопасности
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // Отключаем CSRF для простоты взаимодействия с REST API
                .csrf(csrf -> csrf.disable())
                // Определение правил доступа:
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/book").hasRole("USER")
                        .requestMatchers("/book/v2").hasRole("ADMIN")
                        .requestMatchers("/books").hasRole("USER")
                        .requestMatchers("/api/register", "/login").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults()); // Включение HTTP Basic Authentication
        return httpSecurity.build();
    }

    // Бин для шифрования паролей с использованием BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // Благодаря наличию бина CustomUserDetailsService Spring Security автоматически использует его для аутентификации.

    // Создание пользователей в памяти с использованием безопасного кодирования паролей
//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
//        UserDetails user = User.builder()
//                .username("user1")
//                .password(encoder.encode("password"))
//                .roles("USER")
//                .build();
//        UserDetails admin = User.builder()
//                .username("admin1")
//                .password(encoder.encode("password"))
//                .roles("USER", "ADMIN")
//                .build();
//        return new InMemoryUserDetailsManager(user, admin);
//    }
}