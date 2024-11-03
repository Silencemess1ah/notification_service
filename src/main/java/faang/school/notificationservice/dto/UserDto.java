package faang.school.notificationservice.dto;

import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    private long id;
    private String username;
    private String email;
    private String phone;
    private Long telegramChatId;
    private PreferredContact preference;
    private Locale locale;

    public enum PreferredContact {
        EMAIL, SMS, TELEGRAM, PHONE
    }
}
