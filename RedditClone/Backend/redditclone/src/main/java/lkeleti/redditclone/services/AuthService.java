package lkeleti.redditclone.services;

import lkeleti.redditclone.dtos.RegisterRequestCommand;
import lkeleti.redditclone.models.NotificationEmail;
import lkeleti.redditclone.models.User;
import lkeleti.redditclone.models.VerificationToken;
import lkeleti.redditclone.repositories.UserRepository;
import lkeleti.redditclone.repositories.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;

    @Transactional
    public void signUp(RegisterRequestCommand registerRequestCommand) {
        User user = new User(
                registerRequestCommand.getUserName(),
                passwordEncoder.encode(registerRequestCommand.getPassword()),
                registerRequestCommand.getEmail(),
                LocalDateTime.now(),
                false
        );
        userRepository.save(user);

        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail(
                "Please activate your RedditClone account!",
                user.getEmail(),
                "Thank you for signing up to RedditClone, " +
                        "please click on the below url to activate your account: " +
                        "http://localhost:8080/api/auth/accountVerification/" + token
                ));
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(
                token,
                user,
                LocalDateTime.now().plusHours(1)
        );

        verificationTokenRepository.save(verificationToken);
        return token;
    }
}
