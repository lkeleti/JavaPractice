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
public class RegisterRequestCommand {

    @NotBlank(message = "Email cannot be empty or null")
    @Email(message = "Invalid email format.")
    private String email;

    @NotBlank(message = "Username cannot be empty or null")
    private String userName;

    @NotBlank(message = "Password cannot be empty or null")
    private String password;
}
