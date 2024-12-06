package faang.school.notificationservice.config.redis;

import faang.school.notificationservice.listener.AchievementEventListener;
import faang.school.notificationservice.redis.EventStartEventListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.channel.achievement}")
    private String achievementTopic;

    @Value("${spring.data.redis.channel.event_start}")
    private String eventStartTopic;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration =
                new RedisStandaloneConfiguration(redisHost, redisPort);

        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    MessageListenerAdapter achievementMessageListener(AchievementEventListener achievementEventListener) {
        return new MessageListenerAdapter(achievementEventListener);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        return template;
    }


    @Bean
    ChannelTopic achievementTopic() {
        return new ChannelTopic(achievementTopic);
    }

    @Bean
    RedisMessageListenerContainer redisContainer(AchievementEventListener achievementEventListener,
                                                 EventStartEventListener eventStartEventListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        container.addMessageListener(achievementEventListener, achievementTopic());
        container.addMessageListener(eventStartListener(eventStartEventListener), eventStartTopic());
        return container;
    }

    @Bean
    MessageListenerAdapter eventStartListener(EventStartEventListener eventStartEventListener) {
        return new MessageListenerAdapter(eventStartEventListener);
    }

    @Bean
    ChannelTopic eventStartTopic() {
        return new ChannelTopic(eventStartTopic);
    }
}
