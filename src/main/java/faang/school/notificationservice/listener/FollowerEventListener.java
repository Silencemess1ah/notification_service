package faang.school.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.FollowerEvent;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.messaging.FollowerMessageBuilder;
import faang.school.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowerEventListener implements MessageListener {

    private final ObjectMapper objectMapper;
    private final UserServiceClient userServiceClient;
    private final List<NotificationService> notificationServices;
    private final FollowerMessageBuilder messageBuilder;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            FollowerEvent followerEvent = objectMapper.readValue(message.getBody(), FollowerEvent.class);
            UserDto user = userServiceClient.getUser(followerEvent.getFolloweeId());
            String text = messageBuilder.buildMessage(followerEvent, Locale.getDefault());

            notificationServices.stream()
                .filter(service -> service.getPreferredContact() == user.getPreference())
                .findFirst()
                .ifPresent(service -> service.send(user, text));

        } catch (Exception e) {
            log.error("Ошибка обработки FollowerEvent", e);
        }
    }
}
