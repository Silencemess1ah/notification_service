package faang.school.notificationservice.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDto {
    private long id;
    private String username;

    @Email
    private String email;

    private String phone;
    private PreferredContact preference;

    public enum PreferredContact {
        EMAIL, SMS, TELEGRAM
    }
}
