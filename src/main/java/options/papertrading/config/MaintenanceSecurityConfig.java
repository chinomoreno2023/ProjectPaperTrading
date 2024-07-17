package options.papertrading.config;

import lombok.AllArgsConstructor;
import options.papertrading.services.PersonDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@Profile("maintenance")
public class MaintenanceSecurityConfig extends WebSecurityConfigurerAdapter {
    private final PersonDetailsService personDetailsService;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                .antMatchers("/maintenance").permitAll() // Разрешаем доступ только к странице технических работ
                .anyRequest().authenticated()
                .and()
                .formLogin().disable() // Отключаем форму логина
                .logout().disable() // Отключаем возможность выхода
                .exceptionHandling()
                .defaultAuthenticationEntryPointFor(
                        (request, response, authException) -> response.sendRedirect("/maintenance"),
                        (request) -> true); // Перенаправляем на страницу технических работ при любом запросе
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(personDetailsService)
                .passwordEncoder(getPasswordEncoder());
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}