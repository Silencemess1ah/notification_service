package faang.school.notificationservice.config.redis;

import faang.school.notificationservice.publis.listener.comment.CommentEventListener;
import faang.school.notificationservice.publis.listener.follower.FollowerEventListener;
import faang.school.notificationservice.publis.listener.profile_view.ProfileViewEventListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisProperties redisProperties;
    private final CommentEventListener commentEventListener;
    private final FollowerEventListener followerEventListener;
    private final ProfileViewEventListener profileViewEventListener;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
    }

    @Bean
    public RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer redisContainer = new RedisMessageListenerContainer();
        redisContainer.setConnectionFactory(redisConnectionFactory());

        redisContainer.addMessageListener(commentListenerAdapter(), commentTopic());
        redisContainer.addMessageListener(followerListenerAdapter(), followerTopic());
        redisContainer.addMessageListener(profileViewListenerAdapter(), profileViewTopic());

        return redisContainer;
    }

    @Bean
    MessageListener commentListenerAdapter() {
        return new MessageListenerAdapter(commentEventListener);
    }

    @Bean
    MessageListener followerListenerAdapter() {
        return new MessageListenerAdapter(followerEventListener);
    }

    @Bean
    public MessageListener profileViewListenerAdapter() {
        return new MessageListenerAdapter(profileViewEventListener);
    }

    @Bean
    Topic commentTopic() {
        return new ChannelTopic(redisProperties.getCommentChannelName());
    }

    @Bean
    public Topic followerTopic() {
        return new ChannelTopic(redisProperties.getFollowerEventChannel());
    }

    @Bean
    public Topic profileViewTopic() {
        return new ChannelTopic(redisProperties.getProfileViewChannel());
    }
}
