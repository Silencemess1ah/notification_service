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
        log.info("Получено сообщение из Redis: {}", new String(message.getBody()));

        try {
            FollowerEvent followerEvent = objectMapper.readValue(message.getBody(), FollowerEvent.class);
            log.info("Десериализовано событие: followerId={}, followeeId={}, время события={}",
                followerEvent.getFollowerId(), followerEvent.getFolloweeId(), followerEvent.getEventTime());

            UserDto user = userServiceClient.getUser(followerEvent.getFolloweeId());
            log.info("Получены данные пользователя: userId={}, email={}, предпочтение={}",
                user.getId(), user.getEmail(), user.getPreference());

            String text = messageBuilder.buildMessage(followerEvent, Locale.getDefault());
            log.info("Сформировано сообщение для уведомления: {}", text);

            notificationServices.stream()
                .filter(service -> service.getPreferredContact() == user.getPreference())
                .findFirst()
                .ifPresentOrElse(
                    service -> {
                        service.send(user, text);
                        log.info("Уведомление отправлено через сервис: {}", service.getClass().getSimpleName());
                    },
                    () -> log.warn("Не найден подходящий сервис для отправки уведомления.")
                );

        } catch (Exception e) {
            log.error("Ошибка обработки FollowerEvent", e);
        }
    }
}
