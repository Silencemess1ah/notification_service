package faang.school.notificationservice.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;

@Configuration
public class RedisTopicsFactory {
    @Value("${spring.data.redis.channel-topics.event-start.name}")
    private String eventStartTopicName;

    @Value("${spring.data.redis.channel-topics.profile-view.name}")
    private String profileViewEventTopicName;

    @Value("${spring.data.redis.channel-topics.comment_event.name}")
    private String commentTopicName;
  
    @Value("${spring.data.redis.channel-topics.mentorship.request_accepted}")
    private String mentorshipRequestAcceptedTopicName;

    @Value("${spring.data.redis.channel-topics.post-like.name}")
    private String postLikeTopicName;

    @Bean
    public Topic eventStartTopic() {
        return new ChannelTopic(eventStartTopicName);
    }

    @Bean
    public Topic profileViewEventTopic() {
        return new ChannelTopic(profileViewEventTopicName);
    }

    @Bean
    public Topic commentEventTopic() {
        return new ChannelTopic(commentTopicName);
    }
  
    @Bean  
    public Topic mentorshipRequestAcceptedTopicName() {
        return new ChannelTopic(mentorshipRequestAcceptedTopicName);
    }

    @Bean
    public Topic postLikeTopic() {
        return new ChannelTopic(postLikeTopicName);
    }
}
