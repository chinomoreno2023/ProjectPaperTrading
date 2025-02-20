package redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${redis.database:-1}")
    Integer redisDatabase;

    @Value("${redis.password:}")
    String password;

    @Value("${redis.host:localhost}")
    String host;

    @Value("${redis.port:6379}")
    Integer port;

    @Bean
    public <F, S> RedisTemplate<F, S> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<F, S> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        if (password != null && !password.isEmpty()) {
            config.setPassword(password);
        }
        if (redisDatabase != null && redisDatabase >= 0) {
            config.setDatabase(redisDatabase);
        }
        return new LettuceConnectionFactory(config);
    }

}
