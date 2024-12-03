package faang.school.notificationservice.config.context.redis;

import faang.school.notificationservice.listener.EmailEventListener;
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
    private final RedisConfigProperties redisConfigProperties;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisConfigProperties.host(),
                redisConfigProperties.port());
        return new JedisConnectionFactory(redisConfig);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(JedisConnectionFactory jedisConnectionFactory,
                                                        EmailEventListener emailEventListener) {
       RedisMessageListenerContainer container = new RedisMessageListenerContainer();
       container.setConnectionFactory(jedisConnectionFactory);
       container.addMessageListener(emailEventListener, sendMailTopic());
       return container;
    }

    @Bean
    ChannelTopic sendMailTopic() {
        return new ChannelTopic(redisConfigProperties.sendMailTopic());
    }
}
