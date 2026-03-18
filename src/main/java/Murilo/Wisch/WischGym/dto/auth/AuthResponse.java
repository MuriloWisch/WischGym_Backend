package Murilo.Wisch.WischGym.dto.auth;

import lombok.Getter;

@Getter
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private UserResponse user;

    public AuthResponse(String accessToken, String refreshToken, UserResponse user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }
}
