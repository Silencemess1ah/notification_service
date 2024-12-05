package faang.school.notificationservice.config.context;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.notificationservice.deserializer.LocalDateTimeArrayDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import faang.school.notificationservice.listener.FollowerEventListener;

import java.time.LocalDateTime;

@Slf4j
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.channel.follower}")
    private String followerChannel;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        log.info("Настройка соединения с Redis: хост={}, порт={}", redisHost, redisPort);
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfig);
        try {
            factory.afterPropertiesSet();
            log.info("Соединение с Redis успешно создано.");
        } catch (Exception e) {
            log.error("Ошибка создания соединения с Redis: {}", e.getMessage());
        }
        return factory;
    }

    @Bean
    public RedisMessageListenerContainer container(LettuceConnectionFactory lettuceConnectionFactory,
                                                   MessageListenerAdapter listenerAdapter) {
        log.info("Настройка RedisMessageListenerContainer...");
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(lettuceConnectionFactory);
        container.addMessageListener(listenerAdapter, new ChannelTopic(followerChannel));
        log.info("RedisMessageListenerContainer успешно настроен для канала 'followerChannel'.");
        return container;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        log.info("Создание RedisTemplate для взаимодействия с Redis.");
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory());

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapperForRedisConfig()));

        log.info("RedisTemplate успешно создан и настроен.");
        return redisTemplate;
    }

    @Bean
    public ObjectMapper objectMapperForRedisConfig() {
        log.info("Настройка ObjectMapper для поддержки LocalDateTime.");
        ObjectMapper mapper = new ObjectMapper();

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeArrayDeserializer());
        mapper.registerModule(javaTimeModule);

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        log.info("ObjectMapper успешно настроен.");
        return mapper;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(FollowerEventListener followerEventListener) {
        log.info("Настройка MessageListenerAdapter для обработки сообщений...");
        return new MessageListenerAdapter(followerEventListener, "onMessage");
    }
}
