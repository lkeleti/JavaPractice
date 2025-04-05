package dev.lkeleti.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoanDto {
    private String borrowerName;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BookDto bookDto;
}
