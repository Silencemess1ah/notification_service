package faang.school.notificationservice.service;

import faang.school.notificationservice.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService implements NotificationService {

    private final JavaMailSender mailSender;
//    private final SmsService smsService; // Сервис для SMS
//    private final TelegramService telegramService; // Сервис для Telegram

    @Override
    public UserDto.PreferredContact getPreferredContact() {
        return UserDto.PreferredContact.EMAIL;
    }

    @Override
    public void send(UserDto userDto, String text) {
        System.out.println("Выберите способ отправки уведомления:");
        System.out.println("1. EMAIL");
        System.out.println("2. SMS");
        System.out.println("3. TELEGRAM");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                sendEmail(userDto, text);
                break;
//            case 2:
//                sendSms(userDto, text);
//                break;
//            case 3:
//                sendTelegram(userDto, text);
//                break;
            default:
                System.out.println("Неверный выбор. Попробуйте снова.");
                break;
        }
    }

    private void sendEmail(UserDto userDto, String text) {
        if (userDto.getPreferredContact() == null) {
            log.warn("Предпочтение пользователя не указано, используется значение по умолчанию: EMAIL");
            userDto.setPreferredContact(UserDto.PreferredContact.EMAIL);
        }

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(userDto.getEmail());
            mailMessage.setSubject("Новое уведомление от NotificationService");
            mailMessage.setText(String.format("Здравствуйте, %s!\n\n%s", userDto.getUsername(), text));

            mailSender.send(mailMessage);
            log.info("Письмо успешно отправлено на адрес: {}", userDto.getEmail());
        } catch (MailException e) {
            log.error("Ошибка при отправке письма на адрес {}: {}", userDto.getEmail(), e.getMessage());
        }
    }

//    private void sendSms(UserDto userDto, String text) {
//        if (userDto.getPhoneNumber() == null || userDto.getPhoneNumber().isEmpty()) {
//            logger.warn("Не указан номер телефона для пользователя с ID: {}", userDto.getId());
//            return;
//        }
//
//        smsService.send(userDto, text); // Отправка через SMS сервис
//        logger.info("SMS отправлено на номер: {}", userDto.getPhoneNumber());
//    }
//
//    private void sendTelegram(UserDto userDto, String text) {
//        if (userDto.getTelegramId() == null || userDto.getTelegramId().isEmpty()) {
//            logger.warn("Не указан Telegram ID для пользователя с ID: {}", userDto.getId());
//            return;
//        }
//
//        telegramService.send(userDto, text); // Отправка через Telegram сервис
//        logger.info("Сообщение отправлено в Telegram пользователю с ID: {}", userDto.getTelegramId());
//    }
}
