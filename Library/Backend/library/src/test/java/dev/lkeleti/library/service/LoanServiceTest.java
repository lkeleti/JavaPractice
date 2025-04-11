package dev.lkeleti.library.service;

import dev.lkeleti.library.dto.AuthorDto;
import dev.lkeleti.library.dto.BookDto;
import dev.lkeleti.library.dto.CheckoutRequestDto;
import dev.lkeleti.library.dto.LoanDto;
import dev.lkeleti.library.exception.ResourceNotFoundException;
import dev.lkeleti.library.model.Author;
import dev.lkeleti.library.model.Book;
import dev.lkeleti.library.model.Loan;
import dev.lkeleti.library.repository.BookRepository;
import dev.lkeleti.library.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        authorDto = new AuthorDto(EXISTING_ID, "John Doe", 1980, "British");
        bookToCheckout = new Book("1111" , "Harry Potter", 1999, author);
        book = new Book(EXISTING_ID, "1111" , "Harry Potter", 1999, author);
        bookDto = new BookDto(EXISTING_ID, "1111" , "Harry Potter", 1999, authorDto);
        savedLoan = new Loan("Borrower Fred", LocalDate.of(2025,4,8), LocalDate.of(2025,4,8).plusDays(14), bookToCheckout);
        loanDto = new LoanDto(1l, "Borrower Fred", LocalDate.of(2025,4,8), LocalDate.of(2025,4,8).plusDays(14),null,  bookDto);

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

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
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
                activeLoan.getId(),
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

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
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

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            loanService.returnBook(EXISTING_ID);
        });

        assertTrue(exception.getMessage().contains("Cannot find loan"));

        verify(bookRepository, times(1)).findById(EXISTING_ID);
        verify(loanRepository, times(1)).findByBookIdAndReturnDateIsNull(EXISTING_ID);
        verify(modelMapper, never()).map(any(), eq(LoanDto.class));
    }

    @Test
    @DisplayName("List all loans successfully")
    void testListLoans_Success() {
        List<Loan> mockLoanList = Arrays.asList(
                new Loan(1L, "Person One", LocalDate.of(2024,4,9), LocalDate.of(2024,4,9).plusDays(14),LocalDate.of(2024,4,9).plusDays(7),book),
                new Loan(2L, "Person Two", LocalDate.of(2025,4,9), LocalDate.of(2025,4,9).plusDays(14),null,book)
        );

        List<LoanDto> expectedDtoList = Arrays.asList(
                new LoanDto(1L, "Person One", LocalDate.of(2024,4,9), LocalDate.of(2024,4,9).plusDays(14),LocalDate.of(2024,4,9).plusDays(7),bookDto),
                new LoanDto(2L, "Person Two", LocalDate.of(2025,4,9), LocalDate.of(2025,4,9).plusDays(14),null,bookDto)
        );

        when(loanRepository.findAll()).thenReturn(mockLoanList);

        Type listType = new TypeToken<List<LoanDto>>() {}.getType();
        when(modelMapper.map(mockLoanList, listType)).thenReturn(expectedDtoList);


        List<LoanDto> result = loanService.listLoans();

        assertNotNull(result);
        assertEquals(expectedDtoList.size(), result.size());

        assertEquals(expectedDtoList.get(0).getId(), result.get(0).getId());
        assertEquals(expectedDtoList.get(1).getId(), result.get(1).getId());

        verify(loanRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(mockLoanList, listType);
    }

    @Test
    @DisplayName("List all loans returns empty list when no loans exist")
    void testListLoans_Empty() {

        List<Loan> emptyLoanList = new ArrayList<>();
        List<LoanDto> emptyDtoList = new ArrayList<>();
        Type listType = new TypeToken<List<LoanDto>>() {}.getType();

        when(loanRepository.findAll()).thenReturn(emptyLoanList);
        when(modelMapper.map(emptyLoanList, listType)).thenReturn(emptyDtoList);

        List<LoanDto> result = loanService.listLoans();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(loanRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(emptyLoanList, listType);
    }

    @Test
    @DisplayName("List all active loans successfully")
    void testListActiveLoans_Success() {
        List<Loan> mockActiveLoanList = Arrays.asList(
                new Loan(1L, "Person One", LocalDate.of(2024,4,9), LocalDate.of(2024,4,9).plusDays(14),null,book),
                new Loan(2L, "Person Two", LocalDate.of(2025,4,9), LocalDate.of(2025,4,9).plusDays(14),null,book)
        );

        List<LoanDto> expectedDtoList = Arrays.asList(
                new LoanDto(1L, "Person One", LocalDate.of(2024,4,9), LocalDate.of(2024,4,9).plusDays(14),null,bookDto),
                new LoanDto(2L, "Person Two", LocalDate.of(2025,4,9), LocalDate.of(2025,4,9).plusDays(14),null,bookDto)
        );

        when(loanRepository.findByReturnDateIsNull()).thenReturn(mockActiveLoanList);

        Type listType = new TypeToken<List<LoanDto>>() {}.getType();
        when(modelMapper.map(mockActiveLoanList, listType)).thenReturn(expectedDtoList);


        List<LoanDto> result = loanService.listActiveLoans();

        assertNotNull(result);
        assertEquals(expectedDtoList.size(), result.size());

        assertEquals(expectedDtoList.get(0).getId(), result.get(0).getId());
        assertEquals(expectedDtoList.get(1).getId(), result.get(1).getId());

        verify(loanRepository, times(1)).findByReturnDateIsNull();
        verify(modelMapper, times(1)).map(mockActiveLoanList, listType);
    }

    @Test
    @DisplayName("List all active loans returns empty list when no active loans exist")
    void testListActiveLoans_Empty() {

        List<Loan> emptyLoanList = new ArrayList<>();
        List<LoanDto> emptyDtoList = new ArrayList<>();
        Type listType = new TypeToken<List<LoanDto>>() {}.getType();

        when(loanRepository.findByReturnDateIsNull()).thenReturn(emptyLoanList);
        when(modelMapper.map(emptyLoanList, listType)).thenReturn(emptyDtoList);

        List<LoanDto> result = loanService.listActiveLoans();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(loanRepository, times(1)).findByReturnDateIsNull();
        verify(modelMapper, times(1)).map(emptyLoanList, listType);
    }

    @Test
    @DisplayName("List all overdue loans successfully")
    void testListOverdueLoans_Success() {
        List<Loan> mockOverdueLoanList = Arrays.asList(
                new Loan(1L, "Person One", LocalDate.of(2024,4,9), LocalDate.of(2024,4,9).plusDays(14),null,book),
                new Loan(2L, "Person Two", LocalDate.of(2024,5,9), LocalDate.of(2024,5,9).plusDays(14),null,book)
        );

        List<LoanDto> expectedDtoList = Arrays.asList(
                new LoanDto(1L, "Person One", LocalDate.of(2024,4,9), LocalDate.of(2024,4,9).plusDays(14),null,bookDto),
                new LoanDto(2L, "Person Two", LocalDate.of(2024,5,9), LocalDate.of(2024,5,9).plusDays(14),null,bookDto)
        );

        when(loanRepository.findByReturnDateIsNullAndDueDateBefore(LocalDate.now())).thenReturn(mockOverdueLoanList);

        Type listType = new TypeToken<List<LoanDto>>() {}.getType();
        when(modelMapper.map(mockOverdueLoanList, listType)).thenReturn(expectedDtoList);


        List<LoanDto> result = loanService.listOverdueLoans();

        assertNotNull(result);
        assertEquals(expectedDtoList.size(), result.size());

        assertEquals(expectedDtoList.get(0).getId(), result.get(0).getId());
        assertEquals(expectedDtoList.get(1).getId(), result.get(1).getId());

        verify(loanRepository, times(1)).findByReturnDateIsNullAndDueDateBefore(LocalDate.now());
        verify(modelMapper, times(1)).map(mockOverdueLoanList, listType);
    }

    @Test
    @DisplayName("List all overdue loans returns empty list when no active loans exist")
    void testListOverdueLoans_Empty() {

        List<Loan> emptyLoanList = new ArrayList<>();
        List<LoanDto> emptyDtoList = new ArrayList<>();
        Type listType = new TypeToken<List<LoanDto>>() {}.getType();

        when(loanRepository.findByReturnDateIsNullAndDueDateBefore(LocalDate.now())).thenReturn(emptyLoanList);
        when(modelMapper.map(emptyLoanList, listType)).thenReturn(emptyDtoList);

        List<LoanDto> result = loanService.listOverdueLoans();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(loanRepository, times(1)).findByReturnDateIsNullAndDueDateBefore(LocalDate.now());
        verify(modelMapper, times(1)).map(emptyLoanList, listType);
    }

    @Test
    @DisplayName("List loan history of book successfully")
    void testListLoanHistoryForBook_Success() {
        List<Loan> mockLoanList = Arrays.asList(
                new Loan(1L, "Person One", LocalDate.of(2024,4,9), LocalDate.of(2024,4,9).plusDays(14),LocalDate.of(2024,4,9).plusDays(7),book),
                new Loan(2L, "Person Two", LocalDate.of(2025,4,9), LocalDate.of(2025,4,9).plusDays(14),null,book)
        );

        List<LoanDto> expectedDtoList = Arrays.asList(
                new LoanDto(1L, "Person One", LocalDate.of(2024,4,9), LocalDate.of(2024,4,9).plusDays(14),LocalDate.of(2024,4,9).plusDays(7),bookDto),
                new LoanDto(2L, "Person Two", LocalDate.of(2025,4,9), LocalDate.of(2025,4,9).plusDays(14),null,bookDto)
        );

        when(bookRepository.findById(EXISTING_ID)).thenReturn(Optional.of(book));
        when(loanRepository.findByBookId(EXISTING_ID)).thenReturn(mockLoanList);

        Type listType = new TypeToken<List<LoanDto>>() {}.getType();
        when(modelMapper.map(mockLoanList, listType)).thenReturn(expectedDtoList);


        List<LoanDto> result = loanService.getLoanHistoryForBook(EXISTING_ID);

        assertNotNull(result);
        assertEquals(expectedDtoList.size(), result.size());

        assertEquals(expectedDtoList.get(0).getId(), result.get(0).getId());
        assertEquals(expectedDtoList.get(1).getId(), result.get(1).getId());

        verify(bookRepository, times(1)).findById(EXISTING_ID);
        verify(loanRepository, times(1)).findByBookId(EXISTING_ID);
        verify(modelMapper, times(1)).map(mockLoanList, listType);
    }

    @Test
    @DisplayName("List loan history of book when no book exist")
    void testListLoanHistoryForBook_BookNotFound() {
        when(bookRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            loanService.getLoanHistoryForBook(NON_EXISTENT_ID);
        });

        assertTrue(exception.getMessage().contains("Cannot find book"));

        verify(bookRepository, times(1)).findById(NON_EXISTENT_ID);
        verify(loanRepository, never()).findByBookId(anyLong());
    }
}
