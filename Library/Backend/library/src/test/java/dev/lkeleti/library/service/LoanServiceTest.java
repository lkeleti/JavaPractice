package dev.lkeleti.library.service;

import dev.lkeleti.library.dto.AuthorDto;
import dev.lkeleti.library.dto.BookDto;
import dev.lkeleti.library.dto.CheckoutRequestDto;
import dev.lkeleti.library.dto.LoanDto;
import dev.lkeleti.library.model.Author;
import dev.lkeleti.library.model.Book;
import dev.lkeleti.library.model.Loan;
import dev.lkeleti.library.repository.BookRepository;
import dev.lkeleti.library.repository.LoanRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Book Service")
class LoanServiceTest {
    @Mock
    BookRepository bookRepository;

    @Mock
    LoanRepository loanRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    LoanService loanService;

    private Book bookToCheckout;
    private Book book;
    private BookDto bookDto;
    private Author author;
    private AuthorDto authorDto;
    private Loan savedLoan;
    private LoanDto loanDto;
    private Loan activeLoan;

    private CheckoutRequestDto checkoutRequest;
    private CheckoutRequestDto checkoutNonExistsRequest;

    private final Long EXISTING_ID = 1L;
    private final Long NON_EXISTENT_ID = 99L;

    @BeforeEach
    void setUp() {
        author = new Author(EXISTING_ID, "John Doe", 1980, "British", new ArrayList<>());
        authorDto = new AuthorDto(EXISTING_ID, "John Doe", 1980, "British", new ArrayList<>());
        bookToCheckout = new Book("1111" , "Harry Potter", 1999, author);
        book = new Book(EXISTING_ID, "1111" , "Harry Potter", 1999, author);
        bookDto = new BookDto(EXISTING_ID, "1111" , "Harry Potter", 1999, authorDto);
        savedLoan = new Loan("Borrower Fred", LocalDate.of(2025,4,8), LocalDate.of(2025,4,8).plusDays(14), bookToCheckout);
        loanDto = new LoanDto("Borrower Fred", LocalDate.of(2025,4,8), LocalDate.of(2025,4,8).plusDays(14),null,  bookDto);

        checkoutRequest = new CheckoutRequestDto(EXISTING_ID, "Borrower Fred");
        checkoutNonExistsRequest = new CheckoutRequestDto(NON_EXISTENT_ID, "Borrower Fred");

        activeLoan = new Loan(EXISTING_ID, "Borrower Fred", LocalDate.of(2025,4,8), LocalDate.of(2025,4,8).plusDays(9), null, bookToCheckout);
    }

    @Test
    @DisplayName("Checkout book successfully")
    void testCheckoutBook_Success() {
        when(bookRepository.findById(EXISTING_ID)).thenReturn(Optional.of(bookToCheckout));
        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);
        when(modelMapper.map(savedLoan, LoanDto.class)).thenReturn(loanDto);
        when(loanRepository.findByBookIdAndReturnDateIsNull(EXISTING_ID)).thenReturn(Optional.empty());

        LoanDto result = loanService.checkoutBook(checkoutRequest);

        assertNotNull(result);
        assertEquals(loanDto.getBorrowerName(), result.getBorrowerName());
        assertEquals(loanDto.getLoanDate(), result.getLoanDate());
        assertEquals(loanDto.getDueDate(), result.getDueDate());
        assertEquals(loanDto.getReturnDate(), result.getReturnDate());
        assertEquals(loanDto.getBookDto().getIsbn(), result.getBookDto().getIsbn());

        verify(bookRepository, times(1)).findById(EXISTING_ID);
        verify(loanRepository, times(1)).save(any(Loan.class));
        verify(modelMapper, times(1)).map(savedLoan, LoanDto.class);
        verify(loanRepository, times(1)).findByBookIdAndReturnDateIsNull(EXISTING_ID);
    }

    @Test
    @DisplayName("Checkout when book not exists")
    void testCheckoutBook_NotExists() {
        when(bookRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            loanService.checkoutBook(checkoutNonExistsRequest);
        });

        assertTrue(exception.getMessage().contains("Cannot find book"));

        verify(bookRepository, times(1)).findById(NON_EXISTENT_ID);
        verify(loanRepository, never()).save(any(Loan.class));
        verify(modelMapper, never()).map(any(), eq(LoanDto.class));
    }

    @Test
    @DisplayName("Return book successfully")
    void testReturnBook_Success() {


        LocalDate expectedReturnDate = LocalDate.now();
        LoanDto expectedReturnedLoanDto = new LoanDto(
                activeLoan.getBorrowerName(),
                activeLoan.getLoanDate(),
                activeLoan.getDueDate(),
                expectedReturnDate,
                bookDto
        );

        when(loanRepository.findByBookIdAndReturnDateIsNull(EXISTING_ID)).thenReturn(Optional.of(activeLoan));
        when(bookRepository.findById(EXISTING_ID)).thenReturn(Optional.of(book));
        when(modelMapper.map(activeLoan, LoanDto.class)).thenReturn(expectedReturnedLoanDto);

        LoanDto result = loanService.returnBook(EXISTING_ID);


        assertNotNull(result);

        assertEquals(expectedReturnedLoanDto.getBorrowerName(), result.getBorrowerName());
        assertEquals(expectedReturnedLoanDto.getLoanDate(), result.getLoanDate());
        assertEquals(expectedReturnedLoanDto.getDueDate(), result.getDueDate());

        assertNotNull(result.getReturnDate(), "Return date should be set");

        assertEquals(expectedReturnedLoanDto.getBookDto().getId(), result.getBookDto().getId());


        verify(loanRepository, times(1)).findByBookIdAndReturnDateIsNull(EXISTING_ID);
        verify(modelMapper, times(1)).map(activeLoan, LoanDto.class);
        verify(bookRepository, times(1)).findById(EXISTING_ID);
    }

    @Test
    @DisplayName("Return when book not exists")
    void testReturnBook_BookNotExists() {

        when(bookRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            loanService.returnBook(NON_EXISTENT_ID);
        });

        assertTrue(exception.getMessage().contains("Cannot find book"));

        verify(bookRepository, times(1)).findById(NON_EXISTENT_ID);
        verify(loanRepository, never()).findByBookIdAndReturnDateIsNull(any(Long.class));
        verify(modelMapper, never()).map(any(), eq(LoanDto.class));
    }

    @Test
    @DisplayName("Return when loan not exists")
    void testReturnBook_LoanNotExists() {


        when(bookRepository.findById(EXISTING_ID)).thenReturn(Optional.of(book));
        when(loanRepository.findByBookIdAndReturnDateIsNull(EXISTING_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            loanService.returnBook(EXISTING_ID);
        });

        assertTrue(exception.getMessage().contains("Cannot find loan"));

        verify(bookRepository, times(1)).findById(EXISTING_ID);
        verify(loanRepository, times(1)).findByBookIdAndReturnDateIsNull(EXISTING_ID);
        verify(modelMapper, never()).map(any(), eq(LoanDto.class));
    }
}
