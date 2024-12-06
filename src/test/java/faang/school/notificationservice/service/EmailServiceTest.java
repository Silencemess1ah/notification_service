package faang.school.notificationservice.service;

import faang.school.notificationservice.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setUsername("Test User");
        userDto.setEmail("testuser@example.com");
        userDto.setPreferredContact(UserDto.PreferredContact.EMAIL);
    }

    @Test
    @DisplayName("Отправка письма при наличии валидного email")
    void sendEmail_shouldSendEmail_whenValidEmail() throws Exception {
        String messageText = "This is a test message.";

        Method sendEmailMethod = EmailService.class.getDeclaredMethod("sendEmail", UserDto.class, String.class);
        sendEmailMethod.setAccessible(true);

        sendEmailMethod.invoke(emailService, userDto, messageText);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage sentMessage = captor.getValue();

        assertEquals("testuser@example.com", sentMessage.getTo()[0]);
        assertEquals("Новое уведомление от NotificationService", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("Здравствуйте, Test User!"));
        assertTrue(sentMessage.getText().contains(messageText));
    }

    @Test
    @DisplayName("Обработка исключения MailException при ошибке отправки")
    void sendEmail_shouldHandleMailException_whenErrorOccurs() throws Exception {
        String messageText = "This is a test message.";

        doThrow(new MailException("Mail sending failed") {}).when(mailSender).send(any(SimpleMailMessage.class));

        Method sendEmailMethod = EmailService.class.getDeclaredMethod("sendEmail", UserDto.class, String.class);
        sendEmailMethod.setAccessible(true);

        assertDoesNotThrow(() -> sendEmailMethod.invoke(emailService, userDto, messageText));
    }
}
