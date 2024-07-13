package com.billetera.back.config;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


import com.billetera.back.models.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;



@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration}")
    private Long jwtExpirationDate;

    public String generateToken(Authentication authentication){
        Usuario user = (Usuario) authentication.getPrincipal(); // Asumimos que UserDetails es la implementación de User
        String username = user.getUsername();
        Long userId = user.getId();
        String userNombre= user.getNombre();

        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("userNombre",userNombre) // Añadimos el ID del usuario como un claim              
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(key())
                .compact();
    }

    private Key key(){
        byte[] bytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String getUsername(String token){
        Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        return claims.getSubject();
    }

    public Long getUserId(String token){
        Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        return claims.get("userId", Long.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new JwtTokenException("Token ha expirado!!", e);
        } catch (IllegalArgumentException e) {
            throw new JwtTokenException("Token is invalid", e);
       
        } catch (MalformedJwtException e) {
            throw new JwtTokenException("Invalid JWT token", e);
        } catch (UnsupportedJwtException e) {
            throw new JwtTokenException("JWT token is unsupported", e);
        }
    }

    // Excepción personalizada para manejar errores de JWT
    public static class JwtTokenException extends RuntimeException {
        public JwtTokenException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}



