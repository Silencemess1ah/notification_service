package faang.school.notificationservice.messaging;

import faang.school.notificationservice.dto.FollowerEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class FollowerMessageBuilder implements MessageBuilder<FollowerEvent> {

    private final MessageSource messageSource;

    @Override
    public Class<?> getInstance() {
        return FollowerEvent.class;
    }

    @Override
    public String buildMessage(FollowerEvent event, Locale locale) {
        String eventTimeFormatted = event.getEventTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", locale));
        return messageSource.getMessage(
            "follower.notification",
            new Object[]{event.getFollowerId(), eventTimeFormatted},
            locale
        );
    }
}
