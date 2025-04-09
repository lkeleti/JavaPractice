package dev.lkeleti.library.service;

import dev.lkeleti.library.dto.AuthorDto;
import dev.lkeleti.library.dto.CreateAuthorCommand;
import dev.lkeleti.library.dto.UpdateAuthorCommand;
import dev.lkeleti.library.exception.ResourceNotFoundException;
import dev.lkeleti.library.model.Author;
import dev.lkeleti.library.repository.AuthorRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Author Service")
class AuthorServiceTest {
    @Mock
    AuthorRepository authorRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    AuthorService authorService;

    private Author authorToSave;
    private Author savedAuthor;
    private CreateAuthorCommand createCommand;
    private Author existingAuthor;
    private AuthorDto authorDto;
    private UpdateAuthorCommand updateCommand;
    private AuthorDto updatedAuthorDto;
    private final Long EXISTING_ID = 1L;
    private final Long NON_EXISTENT_ID = 99L;

    @BeforeEach
    void setUp() {
        createCommand = new CreateAuthorCommand("John Doe", 1975, "Hungarian");
        authorToSave = new Author("John Doe", 1975, "Hungarian");
        savedAuthor = new Author(EXISTING_ID, "John Doe", 1975, "Hungarian", new ArrayList<>());
        authorDto = new AuthorDto(EXISTING_ID, "John Doe", 1975, "Hungarian", new ArrayList<>());

        existingAuthor = new Author(EXISTING_ID, "Old Name", 1970, "Old Nationality", new ArrayList<>());
        updateCommand = new UpdateAuthorCommand("New Name", 1980, "New Nationality");
        updatedAuthorDto = new AuthorDto(EXISTING_ID, "New Name", 1980, "New Nationality", new ArrayList<>());
    }

    @Test
    @DisplayName("Create new author successfully")
    void testCreateAuthor_Success() {
        // Arrange (Előkészítés)

        when(authorRepository.save(authorToSave)).thenReturn(savedAuthor);
        when(modelMapper.map(savedAuthor, AuthorDto.class)).thenReturn(authorDto);

        // Act (Végrehajtás)
        AuthorDto result = authorService.createAuthor(createCommand);

        // Assert (Ellenőrzés)
        assertNotNull(result);
        assertEquals(authorDto.getId(), result.getId());
        assertEquals(authorDto.getName(), result.getName());
        assertEquals(authorDto.getBirthYear(), result.getBirthYear());
        assertEquals(authorDto.getNationality(), result.getNationality());

        verify(authorRepository, times(1)).save(eq(authorToSave));
        verify(modelMapper, times(1)).map(savedAuthor, AuthorDto.class);
    }

    @Test
    @DisplayName("Update author successfully when author exists")
    void testUpdateAuthor_Success() {
        when(authorRepository.findById(EXISTING_ID)).thenReturn(Optional.of(existingAuthor));
        when(modelMapper.map(existingAuthor, AuthorDto.class)).thenReturn(updatedAuthorDto);

        AuthorDto result = authorService.updateAuthor(EXISTING_ID, updateCommand);

        assertNotNull(result);

        assertEquals(updatedAuthorDto.getId(), result.getId());
        assertEquals(updatedAuthorDto.getName(), result.getName());
        assertEquals(updatedAuthorDto.getBirthYear(), result.getBirthYear());
        assertEquals(updatedAuthorDto.getNationality(), result.getNationality());


        verify(authorRepository, times(1)).findById(EXISTING_ID);
        verify(modelMapper, times(1)).map(existingAuthor, AuthorDto.class); // Ellenőrzi, hogy a (módosult) existingAuthor lett átadva

        assertEquals("New Name", existingAuthor.getName());
        assertEquals(1980, existingAuthor.getBirthYear());
        assertEquals("New Nationality", existingAuthor.getNationality());
    }

    @Test
    @DisplayName("Update author throws exception when author does not exist")
    void testUpdateAuthor_NotFound() {

        when(authorRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            authorService.updateAuthor(NON_EXISTENT_ID, updateCommand);
        });

        assertTrue(exception.getMessage().contains("Can't find author")); // Vagy pontosabb üzenet ID-val

        verify(authorRepository, times(1)).findById(NON_EXISTENT_ID);
        verify(modelMapper, never()).map(any(Author.class), eq(AuthorDto.class));
    }

    @Test
    @DisplayName("Find author by ID when exists")
    void testFindAuthorById_Exists() {
        // Arrange
        when(authorRepository.findById(EXISTING_ID)).thenReturn(Optional.of(savedAuthor));
        when(modelMapper.map(savedAuthor, AuthorDto.class)).thenReturn(authorDto);

        // Act
        AuthorDto result = authorService.findAuthorById(EXISTING_ID);

        // Assert
        assertNotNull(result);
        assertEquals(authorDto.getId(), result.getId());
        assertEquals(authorDto.getName(), result.getName());
        assertEquals(authorDto.getBirthYear(), result.getBirthYear());
        assertEquals(authorDto.getNationality(), result.getNationality());

        verify(authorRepository, times(1)).findById(EXISTING_ID);
        verify(modelMapper, times(1)).map(savedAuthor, AuthorDto.class);
    }

    @Test
    @DisplayName("Find author by ID when not exists")
    void testFindAuthorById_NotFound() {
        // Arrange
        when(authorRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            authorService.findAuthorById(NON_EXISTENT_ID);
        });

        assertTrue(exception.getMessage().contains("Can't find author"));

        verify(authorRepository, times(1)).findById(NON_EXISTENT_ID);
        verify(modelMapper, never()).map(any(), eq(AuthorDto.class));
    }

    @Test
    @DisplayName("Delete author by ID successfully")
    void testDeleteAuthor() {

        // Act
        assertDoesNotThrow(() -> {
            authorService.deleteAuthor(EXISTING_ID);
        });

        verify(authorRepository, times(1)).deleteById(EXISTING_ID);
    }

    @Test
    @DisplayName("Delete author by ID when author does not exist")
    void testDeleteAuthor_IdDoesNotExist() {

        // Act
        assertDoesNotThrow(() -> {
            authorService.deleteAuthor(NON_EXISTENT_ID);
        });

        verify(authorRepository, times(1)).deleteById(NON_EXISTENT_ID);
    }

    @Test
    @DisplayName("List all authors successfully")
    void testListAuthors_Success() {
        // Arrange (Előkészítés)

        List<Author> mockAuthorList = Arrays.asList(
                new Author(1L, "Author One", 1980, "Nationality A", new ArrayList<>()),
                new Author(2L, "Author Two", 1990, "Nationality B", new ArrayList<>())
        );

        List<AuthorDto> expectedDtoList = Arrays.asList(
                new AuthorDto(1L, "Author One", 1980, "Nationality A", new ArrayList<>()),
                new AuthorDto(2L, "Author Two", 1990, "Nationality B", new ArrayList<>())
        );

        when(authorRepository.findAll()).thenReturn(mockAuthorList);

        Type listType = new TypeToken<List<AuthorDto>>() {}.getType();
        when(modelMapper.map(mockAuthorList, listType)).thenReturn(expectedDtoList);


        // Act (Végrehajtás)
        List<AuthorDto> result = authorService.listAuthors();

        // Assert (Ellenőrzés)
        assertNotNull(result);
        assertEquals(expectedDtoList.size(), result.size());

        assertEquals(expectedDtoList.get(0).getId(), result.get(0).getId());
        assertEquals(expectedDtoList.get(1).getId(), result.get(1).getId());

        verify(authorRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(mockAuthorList, listType);
    }

    @Test
    @DisplayName("List all authors returns empty list when no authors exist")
    void testListAuthors_Empty() {
        // Arrange
        List<Author> emptyAuthorList = new ArrayList<>();
        List<AuthorDto> emptyDtoList = new ArrayList<>();
        Type listType = new TypeToken<List<AuthorDto>>() {}.getType();

        when(authorRepository.findAll()).thenReturn(emptyAuthorList);
        when(modelMapper.map(emptyAuthorList, listType)).thenReturn(emptyDtoList);

        // Act
        List<AuthorDto> result = authorService.listAuthors();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(authorRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(emptyAuthorList, listType);
    }
}