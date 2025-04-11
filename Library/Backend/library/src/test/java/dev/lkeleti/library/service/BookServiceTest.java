package dev.lkeleti.library.service;

import dev.lkeleti.library.dto.*;
import dev.lkeleti.library.exception.ResourceNotFoundException;
import dev.lkeleti.library.model.Author;
import dev.lkeleti.library.model.Book;
import dev.lkeleti.library.repository.AuthorRepository;
import dev.lkeleti.library.repository.BookRepository;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Book Service")
class BookServiceTest {
    @Mock
    BookRepository bookRepository;

    @Mock
    AuthorRepository authorRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    BookService bookService;

    private Book bookToSave;
    private Book savedBook;
    private CreateBookCommand createCommand;
    private Book existingBook;
    private BookDto bookDto;
    private UpdateBookCommand updateCommand;
    private UpdateBookCommand updateNoAuthorCommand;
    private BookDto updatedBookDto;

    private final Long EXISTING_ID = 1L;
    private final Long NON_EXISTENT_ID = 99L;

    private Author author = new Author("John Doe", 1975, "Hungarian");
    private AuthorDto authorDto = new AuthorDto(EXISTING_ID, "John Doe", 1975, "Hungarian");

    @BeforeEach
    void setUp() {
        createCommand = new CreateBookCommand("11111111111111111111", "The Book", 2000, EXISTING_ID);
        bookToSave = new Book("11111111111111111111", "The Book", 2000, author);
        savedBook = new Book("11111111111111111111", "The Book", 2000, author);
        bookDto = new BookDto(EXISTING_ID, "11111111111111111111", "The Book", 2000, authorDto);

        existingBook = new Book(EXISTING_ID, "Old ISBN", "Old Title", 1900, author);
        updateCommand = new UpdateBookCommand("New ISBN", "New Title", 2000, EXISTING_ID);
        updateNoAuthorCommand = new UpdateBookCommand("New ISBN", "New Title", 2000, NON_EXISTENT_ID);
        updatedBookDto = new BookDto(EXISTING_ID, "New ISBN", "New Title", 2000, authorDto);
    }

    @Test
    @DisplayName("Create new book successfully")
    void testCreateBook_Success() {

        when(authorRepository.findById(EXISTING_ID)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);
        when(modelMapper.map(savedBook, BookDto.class)).thenReturn(bookDto);

        BookDto result = bookService.createBook(createCommand);

        assertNotNull(result);
        assertEquals(bookDto.getId(), result.getId());
        assertEquals(bookDto.getIsbn(), result.getIsbn());
        assertEquals(bookDto.getTitle(), result.getTitle());
        assertEquals(bookDto.getPublicationYear(), result.getPublicationYear());

        verify(authorRepository, times(1)).findById(EXISTING_ID);
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(modelMapper, times(1)).map(savedBook, BookDto.class);
    }

    @Test
    @DisplayName("List all books successfully")
    void testListBooks_Success() {
        List<Book> mockBookList = Arrays.asList(
                new Book(1L, "11111", "Book One", 1980, author),
                new Book(2L, "22222", "Book Two", 1990, author)
        );

        List<BookDto> expectedDtoList = Arrays.asList(
                new BookDto(1L, "11111", "Book One", 1980, authorDto),
                new BookDto(2L, "22222", "Book Two", 1990, authorDto)
        );

        when(bookRepository.findAll()).thenReturn(mockBookList);

        Type listType = new TypeToken<List<BookDto>>() {}.getType();
        when(modelMapper.map(mockBookList, listType)).thenReturn(expectedDtoList);


        List<BookDto> result = bookService.listBooks();

        assertNotNull(result);
        assertEquals(expectedDtoList.size(), result.size());

        assertEquals(expectedDtoList.get(0).getId(), result.get(0).getId());
        assertEquals(expectedDtoList.get(1).getId(), result.get(1).getId());

        verify(bookRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(mockBookList, listType);
    }

    @Test
    @DisplayName("List all books returns empty list when no books exist")
    void testListBooks_Empty() {

        List<Book> emptyBookList = new ArrayList<>();
        List<BookDto> emptyDtoList = new ArrayList<>();
        Type listType = new TypeToken<List<BookDto>>() {}.getType();

        when(bookRepository.findAll()).thenReturn(emptyBookList);
        when(modelMapper.map(emptyBookList, listType)).thenReturn(emptyDtoList);

        List<BookDto> result = bookService.listBooks();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(bookRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(emptyBookList, listType);
    }

    @Test
    @DisplayName("Find book by ID when exists")
    void testFindBookById_Exists() {

        when(bookRepository.findById(EXISTING_ID)).thenReturn(Optional.of(savedBook));
        when(modelMapper.map(savedBook, BookDto.class)).thenReturn(bookDto);

        BookDto result = bookService.findBookById(EXISTING_ID);

        // Assert
        assertNotNull(result);
        assertEquals(bookDto.getId(), result.getId());
        assertEquals(bookDto.getIsbn(), result.getIsbn());
        assertEquals(bookDto.getTitle(), result.getTitle());
        assertEquals(bookDto.getPublicationYear(), result.getPublicationYear());

        verify(bookRepository, times(1)).findById(EXISTING_ID);
        verify(modelMapper, times(1)).map(savedBook, BookDto.class);
    }

    @Test
    @DisplayName("Find book by ID when not exists")
    void testFindBookById_NotFound() {

        when(bookRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bookService.findBookById(NON_EXISTENT_ID);
        });

        assertTrue(exception.getMessage().contains("Can't find book"));

        verify(bookRepository, times(1)).findById(NON_EXISTENT_ID);
        verify(modelMapper, never()).map(any(), eq(BookDto.class));
    }

    @Test
    @DisplayName("Delete book by ID successfully")
    void testDeleteBook() {

        assertDoesNotThrow(() -> {
            bookService.deleteBook(EXISTING_ID);
        });

        verify(bookRepository, times(1)).deleteById(EXISTING_ID);
    }

    @Test
    @DisplayName("Delete book by ID when book does not exist")
    void testDeleteBook_IdDoesNotExist() {

        assertDoesNotThrow(() -> {
            bookService.deleteBook(NON_EXISTENT_ID);
        });

        verify(bookRepository, times(1)).deleteById(NON_EXISTENT_ID);
    }

    @Test
    @DisplayName("Update book successfully when book exists")
    void testUpdateBook_Success() {
        when(authorRepository.findById(EXISTING_ID)).thenReturn(Optional.of(author));
        when(bookRepository.findById(EXISTING_ID)).thenReturn(Optional.of(existingBook));
        when(modelMapper.map(existingBook, BookDto.class)).thenReturn(updatedBookDto);

        BookDto result = bookService.updateBook(EXISTING_ID, updateCommand);

        assertNotNull(result);

        assertEquals(updatedBookDto.getId(), result.getId());
        assertEquals(updatedBookDto.getIsbn(), result.getIsbn());
        assertEquals(updatedBookDto.getTitle(), result.getTitle());
        assertEquals(updatedBookDto.getPublicationYear(), result.getPublicationYear());


        verify(authorRepository, times(1)).findById(EXISTING_ID);
        verify(bookRepository, times(1)).findById(EXISTING_ID);
        verify(modelMapper, times(1)).map(existingBook, BookDto.class);

        assertEquals("New ISBN", existingBook.getIsbn());
        assertEquals("New Title", existingBook.getTitle());
        assertEquals(2000, existingBook.getPublicationYear());
    }

    @Test
    @DisplayName("Update book throws exception when book does not exist")
    void testUpdateBook_NotFound() {

        when(bookRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bookService.updateBook(NON_EXISTENT_ID, updateCommand);
        });

        assertTrue(exception.getMessage().contains("Can't find book"));

        verify(bookRepository, times(1)).findById(NON_EXISTENT_ID);
        verify(modelMapper, never()).map(any(Book.class), eq(BookDto.class));
    }

    @Test
    @DisplayName("Update book throws exception when author does not exist")
    void testUpdateBook_AuthorNotFound() {

        when(authorRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());
        when(bookRepository.findById(EXISTING_ID)).thenReturn(Optional.of(existingBook));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bookService.updateBook(EXISTING_ID, updateNoAuthorCommand);
        });

        assertTrue(exception.getMessage().contains("Cannot find author"));

        verify(bookRepository, times(1)).findById(EXISTING_ID);
        verify(authorRepository, times(1)).findById(NON_EXISTENT_ID);
        verify(modelMapper, never()).map(any(Book.class), eq(BookDto.class));
    }
}
