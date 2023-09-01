package options.papertrading.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;;

@Configuration
@ComponentScan("options.papertrading")
public class SpringConfig {

    @Bean
    public CommandLineRunner CommandLineRunnerBean() {
        return args -> {};
    }
}