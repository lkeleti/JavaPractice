package dev.lkeleti.library.service;

import dev.lkeleti.library.dto.CheckoutRequestDto;
import dev.lkeleti.library.dto.LoanDto;
import dev.lkeleti.library.model.Book;
import dev.lkeleti.library.model.Loan;
import dev.lkeleti.library.repository.BookRepository;
import dev.lkeleti.library.repository.LoanRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LoanService {
    private ModelMapper modelMapper;
    private LoanRepository loanRepository;
    private BookRepository bookRepository;

    @Transactional
    public LoanDto checkoutBook(CheckoutRequestDto checkoutRequest) {
        Book book = bookRepository.findById(checkoutRequest.getBookId()).orElseThrow(
                ()-> new EntityNotFoundException("Cannot find book")
        );

        Optional<Loan> existingLoanOpt = loanRepository.findByBookIdAndReturnDateIsNull(checkoutRequest.getBookId());
        if (existingLoanOpt.isPresent()) {
            throw new IllegalStateException("Book with id " + checkoutRequest.getBookId() + " is already on loan.");
        }

        Loan loan = new Loan(
                checkoutRequest.getBorrowerName(),
                LocalDate.now(),
                LocalDate.now().plusDays(14),
                book
        );
        loanRepository.save(loan);
        return modelMapper.map(loan, LoanDto.class);
    }

    @Transactional
    public LoanDto returnBook(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                ()-> new EntityNotFoundException("Cannot find book")
        );

        Loan loan = loanRepository.findByBookIdAndReturnDateIsNull(book.getId()).orElseThrow(
                () -> new EntityNotFoundException("Cannot find loan")
        );

        loan.setReturnDate(LocalDate.now());
        return modelMapper.map(loan, LoanDto.class);
    }

    @Transactional(readOnly = true)
    public List<LoanDto> listLoans() {
        Type targetListType = new TypeToken<List<LoanDto>>(){}.getType();
        return modelMapper.map(loanRepository.findAll(), targetListType);
    }

    @Transactional(readOnly = true)
    public List<LoanDto> listActiveLoans() {
        Type targetListType = new TypeToken<List<LoanDto>>(){}.getType();
        return modelMapper.map(loanRepository.findByReturnDateIsNull(), targetListType);
    }

    @Transactional(readOnly = true)
    public List<LoanDto> listOverdueLoans() {
        Type targetListType = new TypeToken<List<LoanDto>>(){}.getType();
        return modelMapper.map(loanRepository.findByReturnDateIsNullAndDueDateBefore(LocalDate.now()), targetListType);
    }

    @Transactional(readOnly = true)
    public List<LoanDto> getLoanHistoryForBook(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                ()-> new EntityNotFoundException("Cannot find book")
        );

        Type targetListType = new TypeToken<List<LoanDto>>(){}.getType();
        return modelMapper.map(loanRepository.findByBookId(book.getId()), targetListType);
    }
}
