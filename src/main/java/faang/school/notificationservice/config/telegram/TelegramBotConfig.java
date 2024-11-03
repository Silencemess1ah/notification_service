package faang.school.notificationservice.config.telegram;

import faang.school.notificationservice.service.telegram.ChimeraCorporationXBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TelegramBotConfig {
    private final ChimeraCorporationXBotService chimeraCorporationXBotService;

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(chimeraCorporationXBotService);
        log.info("ChimeraCorporationXBot registered successfully.");
        return telegramBotsApi;
    }
}
