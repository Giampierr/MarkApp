package com.MarkApp.MarkApp.servicios;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey signinKey;

    public JwtService(@Value("${jwt.secret}")String secret){
        byte [] keyBytes = Decoders.BASE64.decode(secret);
        this.signinKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generarToken(String idUsuario,String tipoUsuario,String rol){
        Instant ahora = Instant.now();
        Instant expiracion = ahora.plusSeconds(900);

        return Jwts.builder()
                .subject(idUsuario)
                .claim("tipoUsuario",tipoUsuario)
                .claim("rol",rol)
                .issuedAt(Date.from(expiracion))
                .signWith(signinKey)
                .compact();
    }
}
