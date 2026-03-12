package Murilo.Wisch.WischGym.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequest {

    private String RefreshToken;
}
