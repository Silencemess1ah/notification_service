package faang.school.notificationservice.service;

import faang.school.notificationservice.dto.UserContactsDto;

public interface NotificationService {
    void send(UserContactsDto user, String message);
    UserContactsDto.PreferredContact getPreferredContact();
}
