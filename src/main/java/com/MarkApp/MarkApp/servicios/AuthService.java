package com.MarkApp.MarkApp.servicios;

import com.MarkApp.MarkApp.modelo.Colaborador;
import com.MarkApp.MarkApp.repositorio.ColaboradorRepositorio;
import com.MarkApp.MarkApp.repositorio.SupervisorRepositorio;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final ColaboradorRepositorio colaboradorRepositorio;
    private final SupervisorRepositorio supervisorRepositorio;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            ColaboradorRepositorio colaboradorRepositorio,
            SupervisorRepositorio supervisorRepositorio,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.colaboradorRepositorio = colaboradorRepositorio;
        this.supervisorRepositorio = supervisorRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String iniciarSesionColaborador(
            String correo,
            String password) {

        Colaborador colaborador = colaboradorRepositorio
                .findByCorreo(correo)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Credenciales incorrectas"));

        if (!passwordEncoder.matches(
                password, colaborador.getPassword())) {
            throw new IllegalArgumentException(
                    "Credenciales incorrectas");
        }

        return jwtService.generarToken(
                String.valueOf(colaborador.getId()),
                "COLABORADOR",
                "COLABORADOR"
        );
    }
}