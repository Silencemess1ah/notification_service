package faang.school.notificationservice.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.EventStartEventDto;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.exception.NotFoundException;
import faang.school.notificationservice.messaging.MessageBuilder;
import faang.school.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.MappingException;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventStartEventListener implements MessageListener {
    //Выполняется в задаче BJS2-41822
    private final ObjectMapper objectMapper;
    private final UserServiceClient userServiceClient;
    private final List<NotificationService> notificationServices;
    private final List<MessageBuilder> messageBuilders;


    @Override
    public void onMessage(Message message, byte[] pattern) {
        EventStartEventDto eventStartEventDto = getEvent(message);
        log.info("Received event to {} event start", eventStartEventDto.getEventId());

        for (Long userId : eventStartEventDto.getEventParticipants()) {
            UserDto userDto = userServiceClient.getUser(userId);

            String notification = getMessage(eventStartEventDto, Locale.ENGLISH);

            sendNotification(userDto, notification);
        }
    }

    private EventStartEventDto getEvent(Message message) {

        try {
            return objectMapper.readValue(
                    new String(message.getBody()),
                    EventStartEventDto.class);
        } catch (JsonProcessingException e) {
            String exceptionMessage = String.format("Unable to parse event: %s, with message: %s",
                    EventStartEventDto.class.getName(), message);
            MappingException mappingException = new MappingException(exceptionMessage, e);
            log.error(exceptionMessage, e);
            throw mappingException;
        }
    }

    private String getMessage(EventStartEventDto event, Locale userLocale) {

        return messageBuilders.stream()
                .filter(messageBuilder -> messageBuilder.getInstance().equals(EventStartEventDto.class))
                .findFirst()
                .map(messageBuilder -> messageBuilder.buildMessage(event, userLocale))
                .orElseThrow(() -> {
                    String exceptionMessage = String.format("Message wasn't found for event type - %s",
                            event.getClass().getName());
                    NotFoundException e = new NotFoundException(exceptionMessage);
                    log.error(exceptionMessage, e);
                    return e;
                });
    }

    private void sendNotification(UserDto userDto, String message) {

        notificationServices.stream()
                .filter(notificationService ->
                        notificationService.getPreferredContact().equals(userDto.getPreference()))
                .findFirst()
                .orElseThrow(() -> {
                    String exceptionMessage =
                            String.format("Notification service wasn't found for user notification preference - %s",
                                    userDto.getPreference());
                    NotFoundException e = new NotFoundException(exceptionMessage);
                    log.error(exceptionMessage, e);
                    return e;
                })
                .send(userDto, message);
    }
}