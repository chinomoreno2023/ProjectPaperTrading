package options.papertrading.config;

import lombok.AllArgsConstructor;
import options.papertrading.services.PersonDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@Profile("maintenance")
public class MaintenanceSecurityConfig {
    private final PersonDetailsService personDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/maintenance").permitAll() // Разрешаем доступ только к странице технических работ
                        .anyRequest().authenticated() // Все остальные запросы требуют аутентификации
                )
                .formLogin(formLogin -> formLogin
                        .disable() // Отключаем форму логина
                )
                .logout(logout -> logout
                        .disable() // Отключаем возможность выхода
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .defaultAuthenticationEntryPointFor(
                                (request, response, authException) -> response.sendRedirect("/maintenance"),
                                (request) -> true) // Перенаправляем на страницу технических работ при любом запросе
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
