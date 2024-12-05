package faang.school.notificationservice.config.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class EmailServiceConfig {
    private final MailProperties mailProperties;

    @Value("${spring.mail.smtp.auth}")
    private boolean auth;

    @Value("${spring.mail.smtp.starttls.enable}")
    private boolean starttls;


    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        System.out.println("host: " + mailProperties.host());
        System.out.println("port: " + mailProperties.port());
        System.out.println("username: " + mailProperties.username());
        System.out.println("password: " + mailProperties.password());
        System.out.println("protocol: " + mailProperties.protocol());
        System.out.println("auth: " + auth);
        System.out.println("starttls: " + starttls);
        System.out.println("debug: " + mailProperties.debug());

        mailSender.setHost(mailProperties.host());
        mailSender.setPort(mailProperties.port());

        mailSender.setUsername(mailProperties.username());
        mailSender.setPassword(mailProperties.password());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", mailProperties.protocol());
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttls);
        props.put("mail.debug", mailProperties.debug());

        return mailSender;
    }

}
