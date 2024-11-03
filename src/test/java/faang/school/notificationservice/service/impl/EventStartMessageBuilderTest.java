package faang.school.notificationservice.service.impl;

import faang.school.notificationservice.model.event.EventStartEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.time.LocalDateTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventStartMessageBuilderTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private EventStartMessageBuilder eventStartMessageBuilder;

    @Test
    public void testBuildMessage() {
        EventStartEvent event = new EventStartEvent();
        event.setStartDateTime(LocalDateTime.now());
        Locale locale = Locale.ENGLISH;
        String formattedDateTime = event.getStartDateTime().format(EventStartMessageBuilder.formatter);
        when(messageSource.getMessage("event.start.new", new Object[]{formattedDateTime}, locale))
                .thenReturn("Congrats! You've new event with the Id just published.");

        String message = eventStartMessageBuilder.buildMessage(event, locale);
        assertEquals("Congrats! You've new event with the Id just published.", message);
    }
}