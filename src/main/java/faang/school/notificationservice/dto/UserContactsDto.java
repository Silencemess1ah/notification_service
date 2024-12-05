package faang.school.notificationservice.dto;

import lombok.Data;

@Data
public class UserContactsDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private PreferredContact preference;

    public enum PreferredContact {
        EMAIL, SMS, TELEGRAM
    }
}
