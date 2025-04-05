package dev.lkeleti.library.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
@Table(name = "loans")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "borrower_name")
    @NotBlank
    private String borrowerName;

    @Column(name = "loan_date")
    @NotNull
    private LocalDate loanDate;

    @Column(name = "due_date")
    @NotNull
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    @NotNull
    private Book book;

    public Loan(String borrowerName, LocalDate loanDate, LocalDate dueDate, Book book) {
        this.borrowerName = borrowerName;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.book = book;
    }
}
