package faang.school.notificationservice.service;

import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import faang.school.notificationservice.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService implements NotificationService {
    private final VonageClient vonageClient;

    @Value("${vonage.number.message-sender}")
    private String messageSenderPhoneNumber;

    @Override
    public void send(UserDto user, String messageText) {
        TextMessage message = new TextMessage(messageSenderPhoneNumber, user.getPhone(), messageText);
        SmsSubmissionResponse response = vonageClient.getSmsClient().submitMessage(message);
        if (response.getMessages().get(0).getStatus() != MessageStatus.OK) {
            log.error("Message failed with error: {}", response.getMessages().get(0).getErrorText());
        }
        log.info("Message sent successfully");
    }

    @Override
    public UserDto.PreferredContact getPreferredContact() {
        return UserDto.PreferredContact.SMS;
    }
}
