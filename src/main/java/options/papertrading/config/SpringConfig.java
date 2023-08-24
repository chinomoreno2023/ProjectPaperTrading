package options.papertrading.config;

import options.papertrading.util.mappers.PersonMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;;

@Configuration
public class SpringConfig {

    @Bean
    public CommandLineRunner CommandLineRunnerBean() {
        return args -> {};
    }

    @Bean
    public PersonMapper personMapper() {
        return PersonMapper.INSTANCE;
    }
}