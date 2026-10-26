package com.MarkApp.MarkApp.servicios;

import com.MarkApp.MarkApp.dto.ColaboradorCambiosDTO;
import com.MarkApp.MarkApp.modelo.Colaborador;
import com.MarkApp.MarkApp.repositorio.ColaboradorRepositorio;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ColaboradorServicio {
    private final ColaboradorRepositorio colaboradorRepositorio;
    private PasswordEncoder passwordEncoder;

    public ColaboradorServicio(ColaboradorRepositorio colaboradorRepositorio, PasswordEncoder passwordEncoder) {
        this.colaboradorRepositorio = colaboradorRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    public Colaborador registrar(Colaborador colaborador){
        colaborador.setPassword(
                passwordEncoder.encode(colaborador.getPassword())
        );

        return colaboradorRepositorio.save(colaborador);
    }

    public Colaborador actualizarParcialmente(int id, ColaboradorCambiosDTO cambios){
        System.out.println("ID recibido: " + id);
        System.out.println("Nombre recibido: " + cambios.nombres());

        Colaborador colaborador = colaboradorRepositorio.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,"Colaborador no encontrado"
                        ));

        if (cambios.nombres() != null){
            colaborador.setNombres(cambios.nombres());
        }

        if (cambios.apellidos() != null){
            colaborador.setApellidos(cambios.apellidos());
        }

        if(cambios.correo() != null){
            colaborador.setCorreo(cambios.correo());
        }

        if (cambios.cargo() != null){
            colaborador.setCargo(cambios.cargo());
        }

        return colaboradorRepositorio.save(colaborador);
    }


}
