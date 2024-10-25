package faang.school.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.ProfileViewEvent;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.messaging.MessageBuilder;
import faang.school.notificationservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class ProfileViewEventListener extends AbstractEventListener<ProfileViewEvent> {
    private final ObjectMapper objectMapper;
    private final NotificationService notificationServiceImpl;
    private final UserServiceClient userServiceClient;
    private final MessageBuilder<ProfileViewEvent> messageBuilder;

    public ProfileViewEventListener(ObjectMapper objectMapper, UserServiceClient userServiceClient, List list, List list2, ObjectMapper objectMapper1, NotificationService notificationServiceImpl, UserServiceClient userServiceClient1, MessageBuilder<ProfileViewEvent> messageBuilder) {
        super(objectMapper, userServiceClient, list, list2);
        this.objectMapper = objectMapper1;
        this.notificationServiceImpl = notificationServiceImpl;
        this.userServiceClient = userServiceClient1;
        this.messageBuilder = messageBuilder;
    }

    public void onMessage(Message message, byte[] pattern) {
        try {
            log.info("ProfileViewEventListener listens for messages");
            ProfileViewEvent event = objectMapper.readValue(message.getBody(), ProfileViewEvent.class);
            log.info("Aquired message with body: {}", message.getBody());
            UserDto profileAuthor = userServiceClient.getUser(event.getAuthorId());
            notificationServiceImpl.send(profileAuthor, messageBuilder.buildMessage(profileAuthor, event));
        } catch (IOException exception) {
            log.info("Failed to deserialize like event", exception);
        }
    }
}

