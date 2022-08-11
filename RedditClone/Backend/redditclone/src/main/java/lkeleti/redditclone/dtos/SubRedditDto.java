package lkeleti.redditclone.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubRedditDto {
    private Long id;
    private String name;
    private String description;
    private Integer numberOfPosts = null;
}
