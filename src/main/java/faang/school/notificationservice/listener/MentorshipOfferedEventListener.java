package faang.school.notificationservice.listener;

import faang.school.notificationservice.dto.MentorshipOfferedEvent;
import feign.FeignException;
import org.springframework.data.redis.connection.Message;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class MentorshipOfferedEventListener extends AbstractEventListener<MentorshipOfferedEvent> {

    @Retryable(retryFor = FeignException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    @Override
    public void onMessage(Message message, byte[] pattern) {
        MentorshipOfferedEvent event = getEvent(message.getBody(), MentorshipOfferedEvent.class);
        String notification = getMessage(event);
        sendNotification(event.getMentorId(), notification);
    }
}
