package faang.school.notificationservice.redis.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClientMock;
import faang.school.notificationservice.config.redis.listener.RedisContainerMessageListener;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.messaging.MessageBuilder;
import faang.school.notificationservice.redis.event.PostLikeEvent;
import faang.school.notificationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostLikeEventMessageListener extends AbstractEventListener<PostLikeEvent> implements RedisContainerMessageListener {

    @Value("${spring.data.redis.channel-topics.post-like.name}")
    private String postLikeTopic;

    public PostLikeEventMessageListener(UserServiceClientMock userServiceClientMock,
                                        List<MessageBuilder<?>> messageBuilders,
                                        List<NotificationService> notificationServices,
                                        ObjectMapper objectMapper) {
        super(userServiceClientMock, messageBuilders, notificationServices, objectMapper, PostLikeEvent.class);
    }

    @Override
    protected void processEvent(PostLikeEvent event) {
        MessageBuilder<PostLikeEvent> messageBuilder = defineBuilder();
        UserDto user = getUserDto(event.getPostAuthorId());
        String message = messageBuilder.buildMessage(event, user.getLocale());
        sendNotification(event.getPostAuthorId(), message);
    }
    
    @Override
    public MessageListenerAdapter getAdapter() {
        return new MessageListenerAdapter(this);
    }

    @Override
    public Topic getTopic() {
        return new ChannelTopic(postLikeTopic);
    }
}
