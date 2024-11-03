package faang.school.notificationservice.service;

import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.service.telegram.ChimeraCorporationXBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final ChimeraCorporationXBotService corporationXBotService;

    @Override
    public void send(UserDto user, String message) {
        Long chatId = user.getTelegramChatId();
        if (chatId == null) {
            log.warn("Chat ID not found for user: {}", user.getId());
            return;
        }
        corporationXBotService.sendNotification(chatId, message);
    }

    @Override
    public UserDto.PreferredContact getPreferredContact() {
        return UserDto.PreferredContact.TELEGRAM;
    }
}
