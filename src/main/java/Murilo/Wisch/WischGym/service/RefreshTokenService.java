package Murilo.Wisch.WischGym.service;

import Murilo.Wisch.WischGym.domain.RefreshToken;
import Murilo.Wisch.WischGym.domain.User;
import Murilo.Wisch.WischGym.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public RefreshTokenService(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    public RefreshToken createRefreshToken(User user){
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken.setExpiryDate(
                LocalDateTime.now().plusDays(7)
        );

        return repository.save(refreshToken);
    }
}
