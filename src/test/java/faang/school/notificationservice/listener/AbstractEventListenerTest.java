package faang.school.notificationservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.exception.NotFoundException;
import faang.school.notificationservice.messaging.MessageBuilder;
import faang.school.notificationservice.service.NotificationService;
import org.hibernate.MappingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractEventListenerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private NotificationService notificationService;

    @Mock
    private MessageBuilder<EventForTest> messageBuilder;

    private EventListenerForTest eventListener;

    @BeforeEach
    void setUp() {
        eventListener = new EventListenerForTest(objectMapper, userServiceClient,
                List.of(notificationService), List.of(messageBuilder));
    }

    @Test
    void processEventTest() throws JsonProcessingException {
        String eventName = "Test Event";
        long invitedId = 1L;
        String message = "{\"name\": \"Test Event\"}";
        EventForTest expectedEvent = new EventForTest(invitedId, eventName);

        when(objectMapper.readValue(message, EventForTest.class)).thenReturn(expectedEvent);

        eventListener.processEvent(message, EventForTest.class, event ->
                assertEquals("Test Event", event.getName()));

        verify(objectMapper, times(1)).readValue(message, EventForTest.class);
    }

    @Test
    void processEventThrowsJsonProcessingExceptionTest() throws JsonProcessingException {
        String invalidMessage = "invalid json";
        when(objectMapper.readValue(invalidMessage, EventForTest.class)).thenThrow(JsonProcessingException.class);

        assertThrows(MappingException.class, () ->
                eventListener.processEvent(invalidMessage, EventForTest.class, event -> {
                })
        );
    }

    @Test
    void getMessageTest() {
        Locale locale = Locale.ENGLISH;
        String eventName = "Test Event";
        String message = "message";
        long invitedId = 1L;
        EventForTest event = new EventForTest(invitedId, eventName);

        when(messageBuilder.supportEventType()).thenReturn(EventForTest.class);
        when(messageBuilder.buildMessage(event, locale)).thenReturn(message);

        String resultMessage = eventListener.getMessage(event, locale);
        verify(messageBuilder, times(1)).supportEventType();
        verify(messageBuilder, times(1)).buildMessage(event, locale);
        assertEquals(message, resultMessage);
    }

    @Test
    void getMessageThrowsExceptionTest() {
        Locale locale = Locale.ENGLISH;
        String eventName = "Test Event";
        long invitedId = 1L;
        EventForTest event = new EventForTest(invitedId, eventName);

        when(messageBuilder.supportEventType()).thenReturn(null);

        assertThrows(NotFoundException.class, () ->
                eventListener.getMessage(event, locale));
    }

    @Test
    void sendNotificationTest() {
        long invitedId = 1L;
        String message = "message";
        UserDto.PreferredContact prefContact = UserDto.PreferredContact.EMAIL;
        UserDto userDto = UserDto.builder()
                .preference(prefContact)
                .id(invitedId)
                .build();

        when(userServiceClient.getUser(invitedId)).thenReturn(userDto);
        when(notificationService.getPreferredContact()).thenReturn(prefContact);

        eventListener.sendNotification(invitedId, message);

        verify(userServiceClient, times(1)).getUser(invitedId);
        verify(notificationService, times(1)).getPreferredContact();
        verify(notificationService, times(1)).send(userDto, message);
    }

    @Test
    void sendNotificationThrowsExceptionTest() {
        long invitedId = 1L;
        String message = "message";
        UserDto.PreferredContact prefContact = UserDto.PreferredContact.EMAIL;
        UserDto.PreferredContact anotherPrefContact = UserDto.PreferredContact.SMS;
        UserDto userDto = UserDto.builder()
                .preference(prefContact)
                .id(invitedId)
                .build();

        when(userServiceClient.getUser(invitedId)).thenReturn(userDto);
        when(notificationService.getPreferredContact()).thenReturn(anotherPrefContact);

        assertThrows(NotFoundException.class,
                () -> eventListener.sendNotification(invitedId, message));
    }
}