package faang.school.notificationservice.dto;

import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long id;
    private String username;
    private String email;
    private String phone;
    private long telegramChatId;
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
