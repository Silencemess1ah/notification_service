package faang.school.notificationservice.messaging.profile_view;

import faang.school.notificationservice.dto.profile_view.ProfileViewEventDto;
import faang.school.notificationservice.messaging.MessageBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ProfileViewMessageBuilder implements MessageBuilder<ProfileViewEventDto> {
    private final MessageSource messageSource;

    @Override
    public Class<?> getInstance() { return ProfileViewEventDto.class; }

    @Override
    public String buildMessage(ProfileViewEventDto event, Locale locale) {
        Object[] args = { event.requestingId() };
        return messageSource.getMessage("profile_view", args, locale);
    }
}
