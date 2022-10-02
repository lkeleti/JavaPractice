package lkeleti.jpaauthuserh2;

import lkeleti.jpaauthuserh2.models.Post;
import lkeleti.jpaauthuserh2.models.User;
import lkeleti.jpaauthuserh2.repositories.PostRepository;
import lkeleti.jpaauthuserh2.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class Jpaauthuserh2Application {

	public static void main(String[] args) {
		SpringApplication.run(Jpaauthuserh2Application.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public CommandLineRunner commandLineRunner(PostRepository postRepository, UserRepository userRepository, PasswordEncoder passwordEncoder){
		return args-> {
			postRepository.save(new Post("Hello world!", "hello-world","Welcome to my blog!", "lkeleti"));
			userRepository.save(new User("user", passwordEncoder.encode("password"), true, "ROLE_USER"));
			userRepository.save(new User("admin", passwordEncoder.encode("password"), true, "ROLE_USER,ROLE_ADMIN"));
		};
	}
}
