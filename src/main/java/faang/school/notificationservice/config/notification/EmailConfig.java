package faang.school.notificationservice.config.notification;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class EmailConfig {
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public SimpleMailMessage mailMessage() {
        return new SimpleMailMessage();
    }

    @Bean
    public Executor mailExecutor() {
        return Executors.newCachedThreadPool();
    }
}
