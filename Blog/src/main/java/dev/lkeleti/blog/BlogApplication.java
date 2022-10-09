package dev.lkeleti.blog;

import dev.lkeleti.blog.models.Author;
import dev.lkeleti.blog.models.Comment;
import dev.lkeleti.blog.models.Post;
import dev.lkeleti.blog.repositories.AuthorRepository;
import dev.lkeleti.blog.repositories.CommentRepository;
import dev.lkeleti.blog.repositories.PostRepository;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(PostRepository postRepository, AuthorRepository authorRepository, CommentRepository commentRepository) {
		return args-> {
			Author author = authorRepository.save(new Author(
					"John",
					"Doe",
					"johndoe@gmail.com",
					"johndoe"
			));
			Post post = postRepository.save(new Post(
					"Hello World!",
					"Welcome to my blog!",
					author
			));

			Comment comment = new Comment(
					"First Comment",
					"This is the first comment",
					post
			);
			commentRepository.save(comment);

			Comment commentTwo = new Comment(
					"Second Comment",
					"This is the second comment",
					post
			);
			commentRepository.save(commentTwo);
		};
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
