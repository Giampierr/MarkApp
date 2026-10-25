package com.MarkApp.MarkApp.servicios;

import com.MarkApp.MarkApp.modelo.Colaborador;
import com.MarkApp.MarkApp.repositorio.ColaboradorRepositorio;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
