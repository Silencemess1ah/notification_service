package faang.school.notificationservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Locale;

@Data
@Builder
public class UserDto {
    private long id;
    private String username;
    private String email;
    private String phone;
    private PreferredContact preference;
    private Locale locale;

    public enum PreferredContact {
        EMAIL, SMS, TELEGRAM, PHONE
    }
}
