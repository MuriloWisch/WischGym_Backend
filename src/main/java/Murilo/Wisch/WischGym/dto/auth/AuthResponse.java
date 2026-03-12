package Murilo.Wisch.WischGym.dto.auth;

import lombok.Getter;

@Getter
public class AuthResponse {

    private String AccessToken;
    private String RefreshToken;

    public AuthResponse(String accessToken, String refreshToken) {
        AccessToken = accessToken;
        RefreshToken = refreshToken;
    }
}
