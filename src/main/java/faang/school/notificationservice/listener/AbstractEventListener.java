package faang.school.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.messaging.MessageBuilder;
import faang.school.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

@RequiredArgsConstructor
public abstract class AbstractEventListener<T> {
    protected final ObjectMapper objectMapper;
    protected final UserServiceClient userServiceClient;
    private final List<NotificationService> notificationServices;
    protected final List<MessageBuilder<T>> messageBuilders;


    protected void handleEvent(Message message, Class<T> type, Consumer<T> consumer) {
        try {
            T event = objectMapper.readValue(message.getBody(), type);
            consumer.accept(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getMessage(Class<?> eventClass, T event, Locale userLocale) {
        return messageBuilders.stream()
                .filter(builder -> builder.getInstance() == eventClass)
                .findFirst()
                .map(messageBuilder -> messageBuilder.buildMessage(event, userLocale))
                .orElseThrow(() -> new IllegalArgumentException("No message builder found for the event type: " +
                        event.getClass().getName())
                );
    }

    protected void sendNotification(Long receiverId, String message) {
        UserDto user = userServiceClient.getUser(receiverId);
        notificationServices.stream()
                .filter(service -> service.getPreferredContact() == user.getPreference())
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "No notification service found for the user's preferred communication method."
                ))
                .send(user, message);
    }

    public MessageListenerAdapter getListenerAdapter(){
        return new MessageListenerAdapter(this, "onMessage");
    }

    public abstract ChannelTopic getTopic();
}
