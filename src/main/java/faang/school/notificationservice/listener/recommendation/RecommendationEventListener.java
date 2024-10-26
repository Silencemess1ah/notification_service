package faang.school.notificationservice.listener.recommendation;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.recommendation.RecommendationRequestEventDto;
import faang.school.notificationservice.dto.user.UserDto;
import faang.school.notificationservice.listener.AbstractEventListener;
import faang.school.notificationservice.messaging.MessageBuilder;
import faang.school.notificationservice.service.NotificationService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
public class RecommendationEventListener extends AbstractEventListener<RecommendationRequestEventDto> {

    public RecommendationEventListener(ObjectMapper objectMapper, UserServiceClient userServiceClient,
                                       Map<Class<?>, MessageBuilder<?>> messageBuilders,
                                       Map<UserDto.PreferredContact, NotificationService> notificationServices) {
        super(objectMapper, userServiceClient, messageBuilders, notificationServices);
    }

    @Override
    public void onMessage(@NonNull Message message, byte[] pattern) {
        log.info("Got message, trying to handle it");
        handleEvent(message, RecommendationRequestEventDto.class, event -> {
            UserDto user = userServiceClient.getUser(event.getId());
            Locale userPreferedLocale = user.getLocale() != null ? user.getLocale() : Locale.ENGLISH;
            String text = getMessage(event, userPreferedLocale);
            sendNotification(user, text);
        });
    }
}
