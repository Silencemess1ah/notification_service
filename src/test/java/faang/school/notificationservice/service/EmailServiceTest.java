package faang.school.notificationservice.service;

import faang.school.notificationservice.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Spy
    private SimpleMailMessage mailMessage;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendEmail() {
        emailService.send(getUserDto(), "example message");
        Mockito.verify(mailSender).send(mailMessage);
        assertEquals(Optional.of(getUserDto().getEmail()), Arrays.stream(Objects.requireNonNull(mailMessage.getTo())).findFirst());
        assertEquals("example message", mailMessage.getText());
    }

    @Test
    void getPreferredContact() {
        assertEquals(UserDto.PreferredContact.EMAIL, emailService.getPreferredContact());
    }

    private UserDto getUserDto() {
        return UserDto.builder()
                .email("example@gmail.com")
                .build();
    }
}