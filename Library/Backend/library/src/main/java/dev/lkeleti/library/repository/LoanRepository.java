package dev.lkeleti.library.repository;

import dev.lkeleti.library.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    Optional<Loan> findByBookIdAndReturnDateIsNull(Long bookId);
    List<Loan> findByReturnDateIsNull();
    List<Loan> findByReturnDateIsNullAndDueDateBefore(LocalDate date);
    List<Loan> findByBookId(Long bookId);
    List<Loan> findByBorrowerNameContainingIgnoreCase(String borrowerNameFragment);
}
