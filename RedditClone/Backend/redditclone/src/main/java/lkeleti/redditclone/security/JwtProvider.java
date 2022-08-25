package lkeleti.redditclone.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

import static io.jsonwebtoken.Jwts.parserBuilder;

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
        parserBuilder().setSigningKey(getPublicKey()).build().parseClaimsJws(jwt);
        return true;
    }

    private PublicKey getPublicKey() {
        try {

            keyStore = KeyStore.getInstance("JKS");
            char[] pass = ("secret").toCharArray();

            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("springblog.jks");
            if (resourceAsStream == null) {
                throw new IllegalArgumentException("Keyfile not found!");
            }

            keyStore.load(resourceAsStream, pass);
            PublicKey publicKey = keyStore.getCertificate("1").getPublicKey();

            return publicKey;
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new IllegalArgumentException("Exception occured while retrieving public key from keystore");
        }
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = parserBuilder()
                .setSigningKey(getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
