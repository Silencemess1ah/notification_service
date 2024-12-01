package faang.school.notificationservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.exception.NotFoundException;
import faang.school.notificationservice.messaging.MessageBuilder;
import faang.school.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.hibernate.MappingException;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

@RequiredArgsConstructor
public abstract class AbstractEventListener<T> {

    private final ObjectMapper objectMapper;
    private final UserServiceClient userServiceClient;
    private final List<NotificationService> notificationService;
    private final List<MessageBuilder<T>> messageBuilder;

    public void processEvent(String message, Class<T> eventType, Consumer<T> processingEvent) {
        try {
            T event = objectMapper.readValue(message, eventType);
            processingEvent.accept(event);
        } catch (JsonProcessingException e) {
            throw new MappingException(String.format("Unable to parse event: %s, with message: %s",
                    eventType.getName(), message));
        }
    }

    public String getMessage(T event, Locale userLocale) {
        return messageBuilder.stream()
                .filter(messageBuilder -> messageBuilder.supportEventType() == event.getClass())
                .findFirst()
                .map(messageBuilder -> messageBuilder.buildMessage(event, userLocale))
                .orElseThrow(() -> new NotFoundException(
                        String.format("Message wasn't found for event type - %s",
                                event.getClass().getName())));
    }

    public void sendNotification(long receiverId, String message) {
        UserDto user = userServiceClient.getUser(receiverId);
        notificationService.stream()
                .filter(notificationService -> notificationService.getPreferredContact().
                        equals(user.getPreference()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        String.format("Notification service wasn't found for user notification preference - %s",
                                user.getPreference())
                ))
                .send(user, message);
    }
}
