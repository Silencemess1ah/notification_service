package faang.school.notificationservice.messaging;

import faang.school.notificationservice.client.UserServiceClient;
import faang.school.notificationservice.dto.ProfileViewEvent;
import faang.school.notificationservice.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ProfileViewEventMessageBuilder implements MessageBuilder<ProfileViewEvent> {

    private final MessageSource messageSource;
    private final UserServiceClient userServiceClient;

    @Override
    public Class<ProfileViewEvent> supportEventType() {
        return ProfileViewEvent.class;
    }

    @Override
    public String buildMessage(ProfileViewEvent event, Locale locale) {
        UserDto viewer = userServiceClient.getUser(event.getViewerId());
        return messageSource.getMessage("profile_view.new", new Object[]{viewer.getUsername()}, locale);
    }
}
