package faang.school.notificationservice.messaging.profile_view;

import faang.school.notificationservice.dto.profile_view.ProfileViewEventDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ProfileViewMessageBuilderTest {
    @InjectMocks
    private ProfileViewMessageBuilder profileViewMessageBuilder;
    @Mock
    private MessageSource messageSource;

    private final ProfileViewEventDto testEventDto = new ProfileViewEventDto(1L, 2L);

    @Test
    public void testGetInstance() {
        Class<?> actual = profileViewMessageBuilder.getInstance();
        assertEquals(actual, ProfileViewEventDto.class);
    }

    @Test
    public void testBuildMessage() {
        Object[] args = { testEventDto.requestingId() };
        Locale locale = Locale.getDefault();
        String messageKey = "profile_view";
        String expectedMessage = "Hey! Your CorporationX profile info has just been requested by another user (userId: {0})";

        when(messageSource.getMessage(messageKey, args, locale)).thenReturn(expectedMessage);
        String actual = profileViewMessageBuilder.buildMessage(testEventDto, locale);

        verify(messageSource, times(1))
                .getMessage(messageKey, args, locale);
        assertEquals(expectedMessage, actual);
    }
}
