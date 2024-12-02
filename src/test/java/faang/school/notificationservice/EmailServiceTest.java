package faang.school.notificationservice;

import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.service.EmailService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest
public class EmailServiceTest {

    @MockBean
    private JavaMailSender mailSender;

    @Autowired
    private EmailService emailService;

    @Test
    public void testSendEmail() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");

        String messageText = "Test notification message";

        emailService.send(userDto, messageText);

        Mockito.verify(mailSender).send(Mockito.any(SimpleMailMessage.class));
    }
}
