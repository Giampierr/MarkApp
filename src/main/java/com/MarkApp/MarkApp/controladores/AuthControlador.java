package com.MarkApp.MarkApp.controladores;

import com.MarkApp.MarkApp.dto.LoginRequestDTO;
import com.MarkApp.MarkApp.dto.LoginRespuestaDTO;
import com.MarkApp.MarkApp.dto.SolicitudRecuperacionDTO;
import com.MarkApp.MarkApp.servicios.AuthServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/auth")
public class AuthControlador {
    private final AuthServicio authServicio;

    public AuthControlador(AuthServicio authServicio) {
        this.authServicio = authServicio;
    }

    @PostMapping("/login/colaborador")
    public ResponseEntity<LoginRespuestaDTO> loginColaborador(@RequestBody LoginRequestDTO solicitud){
        LoginRespuestaDTO respuesta = authServicio.iniciarSesionColaborador(
                solicitud.correo(),
                solicitud.password()
        );

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/login/supervisor")
    public ResponseEntity<LoginRespuestaDTO> loginSupervisor(@RequestBody LoginRequestDTO solicitud){
        LoginRespuestaDTO respuesta = authServicio.iniciarSesionSupervisor(
                solicitud.correo(),
                solicitud.password()
        );

        return ResponseEntity.ok(respuesta);
    }


}
