package lkeleti.redditclone.services;

import lkeleti.redditclone.dtos.RefreshTokenDto;
import lkeleti.redditclone.exceptions.RefreshTokenNotFoundException;
import lkeleti.redditclone.models.RefreshToken;
import lkeleti.redditclone.repositories.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RefreshTokenService {

    public final ModelMapper modelMapper;
    public final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenDto generateRefreshToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCreatedDate(LocalDateTime.now());

        return modelMapper.map(refreshTokenRepository.save(refreshToken), RefreshTokenDto.class);
    }

    public void validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(
                () -> new RefreshTokenNotFoundException(token)
        );
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
