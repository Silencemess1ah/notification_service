package faang.school.notificationservice.messaging;

import faang.school.notificationservice.listener.RecommendationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;
@Component
@RequiredArgsConstructor
public class EmailMessageBuilder implements MessageBuilder<RecommendationEvent> {

    @Override
    public Class<RecommendationEvent> getInstance() {
        return RecommendationEvent.class;
    }

    @Override
    public String buildMessage(RecommendationEvent event, Locale locale) {
        return "New recommendationRequest from " + event.getRequesterId() +
                " to " + event.getReceiverId();
    }
}
