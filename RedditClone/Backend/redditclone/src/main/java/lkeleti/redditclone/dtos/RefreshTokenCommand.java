package lkeleti.redditclone.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RefreshTokenCommand {

    @NotBlank
    private String refreshToken;
    private String username;
}
