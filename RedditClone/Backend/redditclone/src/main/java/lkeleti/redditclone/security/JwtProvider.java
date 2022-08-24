package lkeleti.redditclone.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

@Service
public class JwtProvider {
    private KeyStore keyStore;

    public String generateToken(Authentication authentication) {
        String username = authentication.getPrincipal().toString();
        return Jwts.builder()
                .setSubject(username)
                .signWith(getPrivateKey())
                .compact();
    }

    private PrivateKey getPrivateKey() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            char[] pass = ("secret").toCharArray();

            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("springblog.jks");
            if (resourceAsStream == null) {
                throw new IllegalArgumentException("Keyfile not found!");
            }

            keyStore.load(resourceAsStream, pass);
            PrivateKey key = (PrivateKey) keyStore.getKey("1", pass);

            return key;

        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableKeyException e) {
            throw new IllegalStateException("Exception occurred while loading keystore");
        }
    }

    public boolean validateToken(String jwt) {
        Jwts.parserBuilder().setSigningKey(getPublickey()).requireSubject(jwt);
        return true;
    }

    private PublicKey getPublickey() {
        try {
            return keyStore.getCertificate("1").getPublicKey();
        } catch (KeyStoreException e) {
            throw new IllegalArgumentException("Exception occured while retrieving public key from keystore");
        }
    }

    public String getUsernameFromJWT(String token) {
        String username = Jwts.parserBuilder()
                .setSigningKey(getPublickey())
                .requireSubject(token).toString();
        return username;
    }
}
