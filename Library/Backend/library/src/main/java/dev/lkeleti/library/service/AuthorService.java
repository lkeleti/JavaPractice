package dev.lkeleti.library.service;

import dev.lkeleti.library.dto.AuthorDto;
import dev.lkeleti.library.dto.CreateAuthorCommand;
import dev.lkeleti.library.dto.UpdateAuthorCommand;
import dev.lkeleti.library.model.Author;
import dev.lkeleti.library.repository.AuthorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@AllArgsConstructor
public class AuthorService {
    private ModelMapper modelMapper;
    private AuthorRepository authorRepository;

    @Transactional
    public AuthorDto createAuthor(CreateAuthorCommand createAuthorCommand) {
        Author author = new Author(
                createAuthorCommand.getName(),
                createAuthorCommand.getBirthYear(),
                createAuthorCommand.getNationality()
        );
        return modelMapper.map(authorRepository.save(author),AuthorDto.class);
    }

    @Transactional(readOnly = true)
    public List<AuthorDto> listAuthors() {
        Type targetListType = new TypeToken<List<AuthorDto>>(){}.getType();
        return modelMapper.map(authorRepository.findAll(), targetListType);
    }

    @Transactional(readOnly = true)
    public AuthorDto findAuthorById(Long id) {
        Author author = authorRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find author")
        );
        return modelMapper.map(author,AuthorDto.class);
    }

    @Transactional
    public AuthorDto updateAuthor(Long id, UpdateAuthorCommand updateAuthorCommand) {
        Author author = authorRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Can't find author")
        );

        author.setName(updateAuthorCommand.getName());
        author.setBirthYear(updateAuthorCommand.getBirthYear());
        author.setNationality(updateAuthorCommand.getNationality());
        return modelMapper.map(author,AuthorDto.class);
    }

    @Transactional
    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }
}
