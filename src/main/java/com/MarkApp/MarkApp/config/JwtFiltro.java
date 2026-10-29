
package com.MarkApp.MarkApp.config;

import com.MarkApp.MarkApp.servicios.JwtServicio;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication
        .UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority
        .SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication
        .WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFiltro extends OncePerRequestFilter {

    private final JwtServicio jwtServicio;

    public JwtFiltro(JwtServicio jwtServicio) {
        this.jwtServicio = jwtServicio;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String encabezado = request.getHeader("Authorization");

        if (encabezado != null && encabezado.startsWith("Bearer ")) {
            String token = encabezado.substring(7);

            if (jwtServicio.esValido(token)
                    && SecurityContextHolder.getContext()
                    .getAuthentication() == null) {

                Claims claims = jwtServicio.extraerClaims(token);

                String idUsuario = claims.getSubject();
                String tipoUsuario =
                        claims.get("tipoUsuario", String.class);
                String rol = claims.get("rol", String.class);

                var autoridad =
                        new SimpleGrantedAuthority("ROLE_" + rol);

                var autenticacion =
                        new UsernamePasswordAuthenticationToken(
                                idUsuario,
                                null,
                                List.of(
                                        autoridad,
                                        new SimpleGrantedAuthority(
                                                "TIPO_" +
                                                        tipoUsuario.toUpperCase()
                                        )
                                )
                        );

                autenticacion.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(autenticacion);
            }
        }

        filterChain.doFilter(request, response);
    }
}
