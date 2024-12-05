package faang.school.notificationservice.service.email;

import faang.school.notificationservice.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender mailSender;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    private UserDto userDto;
    private SimpleMailMessage message;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .email("username@gmail.com")
                .build();
        message = new SimpleMailMessage();
        message.setTo(userDto.getEmail());
        message.setSubject("Notification from Notification Service");
        message.setText("Test");
    }

    @Test
    void send_Positive() {
        emailService.send(userDto, "Test");
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage result = messageCaptor.getValue();
        assertEquals(result, message);
    }

    @Test
    void getPreferredContact_returnEmail() {
        assertEquals(UserDto.PreferredContact.EMAIL, emailService.getPreferredContact());
    }
}