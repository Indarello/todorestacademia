package com.academia.todorestapp.token;


import com.academia.todorestapp.entities.User;
import com.academia.todorestapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    private static final String AUTHORITIES_KEY = "auth";

    private int expirationInMs = 604800000;

    final UserService userService;

    Date now = new Date();

    Date expiryDate = new Date(now.getTime()+expirationInMs);

    private String jwtSecret = "t0d0Li$t";


    public TokenProvider(UserService userService)
    {
        this.userService = userService;
    }

    public String createToken(Authentication authentication){

        User user = (User) userService.findByUsername(authentication.getPrincipal().toString());
        logger.info("Creating token for user "+user.getUsername());
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256,jwtSecret)
                .compact();
    }

    public String getUsernameFromJWT(String token){

        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        logger.info("This token is "+claims.getSubject());

        return claims.getSubject();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;

    }


}
