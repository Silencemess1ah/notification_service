package faang.school.notificationservice.config.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        logger.info("Настройка соединения с Redis: хост={}, порт={}", redisHost, redisPort);
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfig);
        logger.info("Соединение с Redis успешно создано.");
        return factory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        logger.info("Создание RedisTemplate для взаимодействия с Redis.");
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory());

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapperForRedisConfig()));

        logger.info("RedisTemplate успешно создан и настроен.");
        return redisTemplate;
    }

    @Bean
    public ObjectMapper objectMapperForRedisConfig() {
        logger.info("Настройка ObjectMapper для поддержки LocalDateTime.");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        logger.info("ObjectMapper успешно настроен.");
        return mapper;
    }
}
