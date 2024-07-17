package options.papertrading.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan("options.papertrading")
@EnableScheduling
public class SpringConfig {
}

