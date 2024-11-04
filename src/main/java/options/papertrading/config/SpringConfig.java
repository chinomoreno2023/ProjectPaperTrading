package options.papertrading.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan({"options.papertrading", "kafka"})
@EnableScheduling
@EnableRetry
@EnableJpaRepositories(basePackages = {"kafka.consumer.persistence.repository", "options.papertrading.repositories"})
@EntityScan(basePackages = {"kafka.consumer.persistence.entity", "options.papertrading.models"})
public class SpringConfig {

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(10000); // 10 секунды между попытками
        retryTemplate.setBackOffPolicy(backOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(6); // 6 попытки
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }
}