package uz.pdp.appjwtemail.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import uz.pdp.appjwtemail.entity.Roles;

import java.util.Date;
import java.util.Set;

@Component
public class JwtProvider {

    long expireTime = 36 * 100000;
    String secret = "AssalamuAlaykumVaRohmatulloh";

    public String generateToken(String email,Set<Roles> roles) {
        return Jwts
                .builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .claim("roles",roles)
                .setExpiration(new Date(expireTime + System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }


    public String getEmailFromToken(String token) {
        return Jwts
                .parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
