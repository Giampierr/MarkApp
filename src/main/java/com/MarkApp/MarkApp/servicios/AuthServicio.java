package com.MarkApp.MarkApp.servicios;

import com.MarkApp.MarkApp.dto.LoginRespuestaDTO;
import com.MarkApp.MarkApp.enums.Modulos;
import com.MarkApp.MarkApp.modelo.Colaborador;
import com.MarkApp.MarkApp.modelo.Supervisor;
import com.MarkApp.MarkApp.repositorio.ColaboradorRepositorio;
import com.MarkApp.MarkApp.repositorio.SupervisorRepositorio;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class AuthServicio {

    private final ColaboradorRepositorio colaboradorRepositorio;
    private final SupervisorRepositorio supervisorRepositorio;
    private final PasswordEncoder passwordEncoder;
    private final JwtServicio jwtServicio;
    private final AuditoriaServicio auditoriaServicio;

    public AuthServicio(
            ColaboradorRepositorio colaboradorRepositorio,
            SupervisorRepositorio supervisorRepositorio,
            PasswordEncoder passwordEncoder,
            JwtServicio jwtServicio, AuditoriaServicio auditoriaServicio) {

        this.colaboradorRepositorio = colaboradorRepositorio;
        this.supervisorRepositorio = supervisorRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.jwtServicio = jwtServicio;
        this.auditoriaServicio = auditoriaServicio;
    }

    public LoginRespuestaDTO iniciarSesionColaborador(
            String correo,
            String password) {

        Colaborador colaborador = colaboradorRepositorio
                .findByCorreo(correo)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,"Credenciales incorrectas"
                        ));

        if (!passwordEncoder.matches(
                password, colaborador.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,"Credenciales incorrectas"
            );
        }

        String token = jwtServicio.generarToken(
                String.valueOf(colaborador.getId()),
                "Colaborador",
                colaborador.getCargo()
        );

        return new LoginRespuestaDTO(token,"Colaborador","Colaborador");
    }

    public LoginRespuestaDTO iniciarSesionSupervisor(String correo ,String password){

        Supervisor supervisor = supervisorRepositorio.findByCorreo(correo).orElseThrow(()->
                new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,"Credenciales incorrectas"
                ));

        if (!passwordEncoder.matches(password,supervisor.getPassword())){
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,"Credenciales incorrectas"
            );
        }

        String token = jwtServicio.generarToken(
                String.valueOf(supervisor.getIdSupervisor()),
                "Supervisor",
                supervisor.getRolSupervisor().name()
        );
        String detalle = supervisor.getNombre()+"inicio Sesion";
        auditoriaServicio.crearAuditoria(supervisor, Modulos.Login,"Login",detalle);

        return new LoginRespuestaDTO(token,"Supervisor",supervisor.getRolSupervisor().name());

    }
}