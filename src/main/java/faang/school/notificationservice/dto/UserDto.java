package faang.school.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private Long telegramChatId;
    private String phone;
    private PreferredContact preference;

    @JsonCreator
    public UserDto(@JsonProperty("id") long id,
                   @JsonProperty("username") String username,
                   @JsonProperty("email") String email,
                   @JsonProperty("telegramChatId") Long telegramChatId,
                   @JsonProperty("phone") String phone,
                   @JsonProperty("preference") PreferredContact preference) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.telegramChatId = telegramChatId;
        this.phone = phone;
        this.preference = preference;
    }

    public enum PreferredContact {
        EMAIL, SMS, TELEGRAM
    }
}
