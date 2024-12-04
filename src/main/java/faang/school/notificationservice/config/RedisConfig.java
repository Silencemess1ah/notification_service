package faang.school.notificationservice.config;

import faang.school.notificationservice.listener.RecommendationReceivedEventListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    RedisProperties redisProperties;
    RecommendationReceivedEventListener recommendationReceivedEventListener;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisProperties.getHost());
        configuration.setPort(redisProperties.getPort());
        return new JedisConnectionFactory(configuration);
    }

    @Bean
    ChannelTopic recommendationTopic() {
        return new ChannelTopic(redisProperties.getChannel().getRecommendation());
    }

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        container.addMessageListener(recommendationReceivedEventListener, recommendationTopic());
        return container;
    }
}
