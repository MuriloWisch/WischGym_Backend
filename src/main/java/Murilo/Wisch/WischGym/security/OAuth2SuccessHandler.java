package Murilo.Wisch.WischGym.security;

import Murilo.Wisch.WischGym.domain.User;
import Murilo.Wisch.WischGym.repository.UserRepository;
import Murilo.Wisch.WischGym.security.jwt.JwtService;

import Murilo.Wisch.WischGym.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email = (String) attributes.get("email");
        String googleId = (String) attributes.get("sub");

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            response.sendRedirect("http://localhost:4200/login?erro=conta_nao_encontrada");
            return;
        }

        if (user.getGoogleId() == null) {
            user.setGoogleId(googleId);
            userRepository.save(user);
        }

        List<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .toList();

        String accessToken = jwtService.generateToken(user.getEmail(), roles, user.getId());
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

        String redirect = String.format(
                "http://localhost:4200/oauth2/callback?accessToken=%s&refreshToken=%s",
                accessToken, refreshToken
        );

        response.sendRedirect(redirect);
    }
}