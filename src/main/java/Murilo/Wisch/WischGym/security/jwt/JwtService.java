package Murilo.Wisch.WischGym.security.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET = "wischgymchavesecretabemsecreta97";

    private static final long EXPIRATION = 1000 * 60 * 60;

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateTokens(String email) {
        return Jwts.builder().setSubject(email).setIssuedAt
                (new Date()).setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public String extractEmail(String token){
        return Jwts.parserBuilder().setSigningKey(key)
                .build().parseClaimsJwt(token).getBody().getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            extractEmail(token);
            return true;
        }catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

}
