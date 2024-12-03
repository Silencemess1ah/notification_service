package faang.school.notificationservice.config.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class JacksonConfig {

    @Bean(name = "customObjectMapper")
    public ObjectMapper objectMapper() {
        log.info("Настройка ObjectMapper с поддержкой Java Time API.");

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());
        log.info("Модуль JavaTimeModule зарегистрирован.");

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        log.info("Даты будут сериализованы в ISO-8601 формате, без использования timestamps.");

        return objectMapper;
    }
}
