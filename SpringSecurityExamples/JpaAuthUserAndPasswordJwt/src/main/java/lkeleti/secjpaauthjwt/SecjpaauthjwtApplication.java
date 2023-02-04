package lkeleti.secjpaauthjwt;

import lkeleti.secjpaauthjwt.configs.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class SecjpaauthjwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecjpaauthjwtApplication.class, args);
	}

}
