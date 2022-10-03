package lkeleti.jpaauthuserh2;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
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
@OpenAPIDefinition(info = @Info(title = "Employees API", version = "2.0", description = "Employees Information"))
@SecurityScheme(name = "jpaauthuserh2", scheme = "basic", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
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
