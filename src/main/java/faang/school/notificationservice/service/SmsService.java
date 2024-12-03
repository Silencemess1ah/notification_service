package faang.school.notificationservice.service;

import com.vonage.client.VonageClient;
import com.vonage.client.VonageClientException;
import com.vonage.client.VonageResponseParseException;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import faang.school.notificationservice.dto.UserDto;
import faang.school.notificationservice.exception.SmsSendingException;
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
        try {
            SmsSubmissionResponse response = vonageClient.getSmsClient().submitMessage(message);
            response.getMessages().forEach(msg -> {
                if (msg.getStatus() != MessageStatus.OK) {
                    log.warn("Message failed: {}", msg.getErrorText());
                    throw new SmsSendingException("Message failed with status: " + msg.getStatus() + " and error: " +
                            msg.getErrorText());
                }
                log.info("Message sent successfully: {}", msg.getId());
            });
        } catch (VonageResponseParseException | VonageClientException e) {
            log.error("Failed to send SMS: {}", e.getMessage(), e);
        }
    }

    @Override
    public UserDto.PreferredContact getPreferredContact() {
        return UserDto.PreferredContact.SMS;
    }
}
