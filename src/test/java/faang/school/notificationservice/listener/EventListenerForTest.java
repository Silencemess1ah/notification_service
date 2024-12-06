package faang.school.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.messaging.MessageBuilder;
import faang.school.notificationservice.service.NotificationService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.util.List;

public class EventListenerForTest extends AbstractEventListener<EventForTest> {

    public EventListenerForTest(ObjectMapper objectMapper,
                                UserServiceClient userServiceClient,
                                List<NotificationService> notificationService,
                                List<MessageBuilder<EventForTest>> messageBuilder) {
        super(objectMapper, userServiceClient, notificationService, messageBuilder);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {

    }

    @Override
    public MessageListenerAdapter getAdapter() {
        return null;
    }

    @Override
    public ChannelTopic getTopic() {
        return null;
    }
}
