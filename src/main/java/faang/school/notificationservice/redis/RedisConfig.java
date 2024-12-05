package faang.school.notificationservice.redis;

import faang.school.notificationservice.listener.impl.MentorshipAcceptedEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisConfig {

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    RedisMessageListenerContainer redisContainer(MentorshipAcceptedEventListener mentorshipAcceptedEventListener) {
        RedisMessageListenerContainer redisContainer = new RedisMessageListenerContainer();
        redisContainer.setConnectionFactory(jedisConnectionFactory());
        redisContainer.addMessageListener(mentorshipAcceptedEventListener.getAdapter(), mentorshipAcceptedEventListener.getTopic());
        return redisContainer;
    }
}
