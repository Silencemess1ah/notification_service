package faang.school.notificationservice.config.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {

    private Channels channels;

    @Getter
    @Setter
    protected static class Channels {
        private String follower;
        private String achievementEvent;
        private String goalCompletedEvent;
        private String likePostChannel;
        private String newComment;
        private String recommendationRequestEvent;
        private String profileView;
        private String skillAcquiredChannel;
        private String projectChannel;
    }
}
