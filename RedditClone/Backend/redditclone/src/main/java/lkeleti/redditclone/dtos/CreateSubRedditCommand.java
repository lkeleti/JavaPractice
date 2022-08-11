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
public class CreateSubRedditCommand {

    @NotBlank
    private String name;
    @NotBlank
    private String description;
}
