package Murilo.Wisch.WischGym.service;

import Murilo.Wisch.WischGym.domain.RefreshToken;
import Murilo.Wisch.WischGym.domain.User;
import Murilo.Wisch.WischGym.repository.RefreshTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public void deleteByToken(String token){

        RefreshToken refreshToken =
                repository.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException("Refresh token não encontrado"));

        repository.delete(refreshToken);
    }

    public RefreshToken validarERotacionar(String token) {
        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Refresh token inválido."));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            repository.delete(refreshToken);
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Refresh token expirado. Faça login novamente.");
        }

        repository.delete(refreshToken);
        return createRefreshToken(refreshToken.getUser());
    }
}
