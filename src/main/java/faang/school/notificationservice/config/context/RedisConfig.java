package faang.school.notificationservice.config.context;

import faang.school.notificationservice.listener.CreateRequestEventListener;
import faang.school.notificationservice.listener.FollowerEventListener;
import faang.school.notificationservice.listener.OpenAccountEventListener;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
public class RedisConfig {
    private final CreateRequestEventListener createRequestEventListener;
    private final OpenAccountEventListener openAccountEventListener;

    @Value("${spring.data.redis.host}")
    private String redisHost;
    @Value("${spring.data.redis.port}")
    private int redisPort;
    @Value("${spring.data.redis.channel.follower}")
    private String followerChannelName;
    @Value("${spring.data.redis.channel.create_request}")
    private String createRequestChannelName;
    @Value("${spring.data.redis.channel.open_account}")
    private String openAccountChannelName;


    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new JedisConnectionFactory(redisConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    MessageListenerAdapter followerListener(FollowerEventListener followerEventListener) {
        return new MessageListenerAdapter(followerEventListener);
    }

    @Bean
    MessageListenerAdapter createRequestListener(CreateRequestEventListener createRequestEventListener) {
        return new MessageListenerAdapter(createRequestEventListener);
    }

    @Bean
    MessageListenerAdapter openRequestListener(OpenAccountEventListener openAccountEventListener) {
        return new MessageListenerAdapter(openAccountEventListener);
    }

    @Bean
    ChannelTopic followerTopic() {
        return new ChannelTopic(followerChannelName);
    }

    @Bean
    ChannelTopic createRequestTopic() {
        return new ChannelTopic(createRequestChannelName);
    }

    @Bean
    ChannelTopic openAccountTopic() {
        return new ChannelTopic(openAccountChannelName);
    }

    @Bean
    RedisMessageListenerContainer redisContainer(MessageListenerAdapter followerListener) {
        RedisMessageListenerContainer container
                = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        container.addMessageListener(followerListener, followerTopic());
        container.addMessageListener(createRequestListener(createRequestEventListener), createRequestTopic());
        container.addMessageListener(openRequestListener(openAccountEventListener), openAccountTopic());
        return container;
    }
}
