package faang.school.notificationservice.messaging;

import faang.school.notificationservice.event.RecommendationReceivedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class RecommendationMessageBuilder implements MessageBuilder<RecommendationReceivedEvent> {
    private final MessageSource messageSource;

    @Override
    public Class<?> getInstance() {
        return null;
    }

    @Override
    public String buildMessage(RecommendationReceivedEvent event, Locale locale, Object[] placeholders) {
        return messageSource.getMessage("recommendation.new", placeholders, locale);
    }
}
