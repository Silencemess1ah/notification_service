package faang.school.notificationservice.service;

import faang.school.notificationservice.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService implements NotificationService {

    private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Override
    public UserDto.PreferredContact getPreferredContact() {
        return UserDto.PreferredContact.EMAIL;
    }

    @Override
    public void send(UserDto userDto, String text) {
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            logger.warn("Не указан адрес электронной почты для пользователя с ID: {}", userDto.getId());
            return;
        }

        if (userDto.getPreferredContact() == null) {
            logger.warn("Предпочтение пользователя не указано, используется значение по умолчанию: EMAIL");
            userDto.setPreferredContact(UserDto.PreferredContact.EMAIL);
        }

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(userDto.getEmail());
            mailMessage.setSubject("Новое уведомление от NotificationService");
            mailMessage.setText(String.format("Здравствуйте, %s!\n\n%s", userDto.getName(), text));

            mailSender.send(mailMessage);
            logger.info("Письмо успешно отправлено на адрес: {}", userDto.getEmail());
        } catch (MailException e) {
            logger.error("Ошибка при отправке письма на адрес {}: {}", userDto.getEmail(), e.getMessage());
        }
    }
}
