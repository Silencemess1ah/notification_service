package faang.school.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.UserContactsDto;
import faang.school.notificationservice.event.RecommendationReceivedEvent;
import faang.school.notificationservice.messaging.RecommendationMessageBuilder;
import faang.school.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class RecommendationReceivedEventListener implements MessageListener {
    private final ObjectMapper objectMapper;
    private final UserServiceClient userServiceClient;
    private final RecommendationMessageBuilder recommendationMessageBuilder;
    private final List<NotificationService> notificationServices;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            RecommendationReceivedEvent event = objectMapper.readValue(message.getBody(), RecommendationReceivedEvent.class);
            handleEvent(event);
        } catch (IOException e) {
            log.error("Error while serializing {} from redis. Error: {}", message.getBody(), e.getMessage(), e);
        }
    }

    private void handleEvent(RecommendationReceivedEvent event) {
        UserContactsDto receiverDto = userServiceClient.getUserContacts(event.getReceiverId());
        UserContactsDto authorDto = userServiceClient.getUserContacts(event.getAuthorId());

        Object[] names = {receiverDto.getUsername(), authorDto.getUsername()};

        String message = recommendationMessageBuilder.buildMessage(event, LocaleContextHolder.getLocale(), names);

        notificationServices.stream()
                .filter(service -> receiverDto.getPreference() == service.getPreferredContact())
                .findFirst()
                .ifPresent(service -> service.send(receiverDto, message));
    }
}
