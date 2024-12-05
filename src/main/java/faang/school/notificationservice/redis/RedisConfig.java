package faang.school.notificationservice.redis;

import faang.school.notificationservice.listener.RedisContainerMessageListener;
import faang.school.notificationservice.listener.impl.MentorshipAcceptedEventListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final List<RedisContainerMessageListener> listeners;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    RedisMessageListenerContainer redisContainer(MentorshipAcceptedEventListener mentorshipAcceptedEventListener) {
        RedisMessageListenerContainer redisContainer = new RedisMessageListenerContainer();
        redisContainer.setConnectionFactory(jedisConnectionFactory());
        listeners.forEach(listener -> redisContainer.addMessageListener(listener.getAdapter(), listener.getTopic()));
        return redisContainer;
    }
}
