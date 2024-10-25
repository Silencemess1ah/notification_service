package faang.school.notificationservice.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ProfileViewEvent {
    private final Long viewerId;
    private final Long authorId;
}
