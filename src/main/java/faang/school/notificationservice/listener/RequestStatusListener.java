package faang.school.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.dto.event.RequestStatusEvent;
import faang.school.notificationservice.messaging.MessageBuilder;
import faang.school.notificationservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class RequestStatusListener extends AbstractEventListener<RequestStatusEvent> {


    public RequestStatusListener(ObjectMapper objectMapper,
                                 UserServiceClient userServiceClient,
                                 List<NotificationService> notificationServices,
                                 List<MessageBuilder<RequestStatusEvent>> messageBuilders) {
        super(objectMapper, userServiceClient, notificationServices, messageBuilders);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            log.info("Request status listener received a message");

            RequestStatusEvent event = objectMapper.readValue(message.getBody(), RequestStatusEvent.class);
            long createdBy = event.getCreatedBy();

            UserDto user = userServiceClient.getUser(createdBy);
            String text = getMessage(user, event);
            sendNotification(user, text);

            log.info("Follower listener sent a message to user {}", createdBy);
        } catch (IOException exception) {
            log.info("Failed to deserialize follower event", exception);
        }
    }
}
