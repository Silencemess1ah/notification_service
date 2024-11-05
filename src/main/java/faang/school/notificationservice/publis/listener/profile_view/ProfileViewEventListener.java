package faang.school.notificationservice.publis.listener.profile_view;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.profile_view.ProfileViewEventDto;
import faang.school.notificationservice.messaging.MessageBuilder;
import faang.school.notificationservice.publis.listener.AbstractEventListener;
import faang.school.notificationservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public class ProfileViewEventListener extends AbstractEventListener<ProfileViewEventDto> implements MessageListener {
    public ProfileViewEventListener(ObjectMapper objectMapper,
                                    UserServiceClient userServiceClient,
                                    List<MessageBuilder<ProfileViewEventDto>> builders,
                                    List<NotificationService> services) {
        super(objectMapper, userServiceClient, builders, services);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String received = new String(message.getBody());
        log.info("Received message: {}", received);

        ProfileViewEventDto event = mapToEvent(received, ProfileViewEventDto.class);
        String messageToSend = getMessage(event, Locale.getDefault());
        log.info("Successfully formed a message: {}", messageToSend);

        sendNotification(event.requestedId(), messageToSend);
        log.info("Successfully sent a message to (userId: {})", event.requestedId());
    }
}
