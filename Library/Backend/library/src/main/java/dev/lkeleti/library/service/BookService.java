package dev.lkeleti.library.service;

import dev.lkeleti.library.dto.BookDto;
import dev.lkeleti.library.dto.CreateBookCommand;
import dev.lkeleti.library.dto.UpdateBookCommand;
import dev.lkeleti.library.model.Author;
import dev.lkeleti.library.model.Book;
import dev.lkeleti.library.repository.AuthorRepository;
import dev.lkeleti.library.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;
@Service
@AllArgsConstructor
public class BookService {    private ModelMapper modelMapper;
    private BookRepository bookRepository;
    private AuthorRepository authorRepository;

    @Transactional
    public BookDto createBook(CreateBookCommand createBookCommand) {
        Author author = authorRepository.findById(createBookCommand.getAuthorId()).orElseThrow(
                ()-> new EntityNotFoundException("Cannot find author")
        );

        Book book = new Book(
                createBookCommand.getIsbn(),
                createBookCommand.getTitle(),
                createBookCommand.getPublicationYear(),
                author
        );
        return modelMapper.map(bookRepository.save(book),BookDto.class);
    }

    @Transactional(readOnly = true)
    public List<BookDto> listBooks() {
        Type targetListType = new TypeToken<List<BookDto>>(){}.getType();
        return modelMapper.map(bookRepository.findAll(), targetListType);
    }

    @Transactional(readOnly = true)
    public BookDto findBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book")
        );
        return modelMapper.map(book,BookDto.class);
    }

    @Transactional
    public BookDto updateBook(Long id, UpdateBookCommand updateBookCommand) {
        Book book = bookRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Can't find book")
        );

        Author author = authorRepository.findById(updateBookCommand.getAuthorId()).orElseThrow(
                ()-> new EntityNotFoundException("Cannot find author")
        );

        book.setIsbn(updateBookCommand.getIsbn());
        book.setTitle(updateBookCommand.getTitle());
        book.setPublicationYear(updateBookCommand.getPublicationYear());
        book.setAuthor(author);
        return modelMapper.map(book,BookDto.class);
    }

    @Transactional
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}