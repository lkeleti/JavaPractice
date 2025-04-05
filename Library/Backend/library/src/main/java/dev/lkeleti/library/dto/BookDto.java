package dev.lkeleti.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookDto {

    private Long id;
    private String isbn;
    private String title;
    private Integer publicationYear;
    private AuthorDto author;
}
