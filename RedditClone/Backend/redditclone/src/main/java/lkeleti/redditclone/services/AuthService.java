package lkeleti.redditclone.services;

import io.jsonwebtoken.Jwts;
import lkeleti.redditclone.dtos.AuthenticationResponse;
import lkeleti.redditclone.dtos.LoginRequestCommand;
import lkeleti.redditclone.dtos.MessageDto;
import lkeleti.redditclone.dtos.RegisterRequestCommand;
import lkeleti.redditclone.exceptions.InvalidTokenException;
import lkeleti.redditclone.models.NotificationEmail;
import lkeleti.redditclone.models.User;
import lkeleti.redditclone.models.VerificationToken;
import lkeleti.redditclone.repositories.UserRepository;
import lkeleti.redditclone.repositories.VerificationTokenRepository;
import lkeleti.redditclone.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
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

    @Transactional
    public MessageDto verifyAccount(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token).orElseThrow(
                InvalidTokenException::new
        );

        User user = verificationToken.getUser();

        user.setEnabled(true);
        return new MessageDto("Account activated successfully!");
    }

    public AuthenticationResponse login(LoginRequestCommand loginRequestCommand) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestCommand.getUserName(), loginRequestCommand.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);
        return new AuthenticationResponse(token, loginRequestCommand.getUserName());
    }

    public User getCurrentUser() {
        //TODo not implemented!
        return null;
    }
}
