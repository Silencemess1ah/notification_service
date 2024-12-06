package faang.school.notificationservice.service;

import faang.school.notificationservice.dto.UserDto;
import org.springframework.stereotype.Service;

@Service
public class TelegramService implements NotificationService{
    //Выполняется в задаче BJS2-41837
    @Override
    public void send(UserDto user, String message) {
        System.out.println("Сообщение отправелно в телегу " + message + " пользователю " + user.getUsername());
    }

    @Override
    public UserDto.PreferredContact getPreferredContact() {
        return UserDto.PreferredContact.TELEGRAM;
    }
}
