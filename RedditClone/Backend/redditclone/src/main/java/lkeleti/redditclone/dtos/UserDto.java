package lkeleti.redditclone.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    private Long userId;
    private String userName;
    private String email;
    private LocalDateTime createdDate;
    private boolean enabled;
}
