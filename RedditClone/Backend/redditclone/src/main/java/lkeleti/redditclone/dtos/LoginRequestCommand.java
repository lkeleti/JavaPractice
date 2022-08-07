package lkeleti.redditclone.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginRequestCommand {

    @NotBlank(message = "Username cannot be empty or null")
    private String userName;

    @NotBlank(message = "Password cannot be empty or null")
    private String password;
}
