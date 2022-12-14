package lkeleti.redditclone.services;

import lkeleti.redditclone.dtos.*;
import lkeleti.redditclone.exceptions.InvalidTokenException;
import lkeleti.redditclone.models.NotificationEmail;
import lkeleti.redditclone.models.User;
import lkeleti.redditclone.models.VerificationToken;
import lkeleti.redditclone.repositories.UserRepository;
import lkeleti.redditclone.repositories.VerificationTokenRepository;
import lkeleti.redditclone.security.JwtProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final MailContentBuilder mailContentBuilder;
    private AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    private final RefreshTokenService refreshTokenService;
    @Transactional
    public void signUp(RegisterRequestCommand registerRequestCommand) {
        String password = registerRequestCommand.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        log.error(password);
        log.error(encodedPassword);
        User user = new User(
                registerRequestCommand.getUserName(),
                encodedPassword,
                registerRequestCommand.getEmail(),
                LocalDateTime.now(),
                false
        );
        userRepository.save(user);

        String token = generateVerificationToken(user);
        String message = mailContentBuilder.build("Thank you for signing up to Spring Reddit, please click on the below url to activate your account : "
                + "http://localhost:8080/api/auth/accountVerification/" + token);

        mailService.sendMail(new NotificationEmail(
                "Please activate your RedditClone account!",
                user.getEmail(), message));
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
        String username = loginRequestCommand.getUserName();
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, loginRequestCommand.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);
        return new AuthenticationResponse(token, refreshTokenService.generateRefreshToken().getToken(), Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()), username);
    }

    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return userRepository.findByUserName(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getUsername()));
    }

    public AuthenticationResponse refreshTokens(RefreshTokenCommand refreshTokenCommand) {
        refreshTokenService.validateRefreshToken(refreshTokenCommand.getRefreshToken());
        String token = jwtProvider.generateTokenWithUserName(refreshTokenCommand.getUsername());
        return new AuthenticationResponse(token, refreshTokenCommand.getRefreshToken(), Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()), refreshTokenCommand.getUsername());
    }
}
