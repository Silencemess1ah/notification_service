package faang.school.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.messaging.MessageBuilder;
import faang.school.notificationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventListener extends AbstractEventListener<RecommendationEvent> implements MessageListener {
    private final UserServiceClient userServiceClient;
    @Value("${spring.data.redis.channel-topic}")
    private String topic;

    public EventListener(List<NotificationService> notificationServices,
                         List<MessageBuilder<RecommendationEvent>> messageBuilders,
                         ObjectMapper objectMapper,
                         UserServiceClient userServiceClient) {
        super(notificationServices, messageBuilders, objectMapper, userServiceClient);
        this.userServiceClient = userServiceClient;
    }

    @Override
    public ChannelTopic getChannelTopic() {
        return new ChannelTopic(topic);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        handleEvent(message, RecommendationEvent.class, event -> {
            UserDto requesterId = userServiceClient.getUser(event.getRequesterId());
            String text = getMessage(RecommendationEvent.class, event, requesterId.getLocale());
            sendNotification(event.getReceiverId(), text);
        });
    }
}
