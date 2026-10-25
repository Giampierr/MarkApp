package com.MarkApp.MarkApp.servicios;

import com.MarkApp.MarkApp.modelo.Supervisor;
import com.MarkApp.MarkApp.repositorio.SupervisorRepositorio;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SupervisorServicio {
    private final SupervisorRepositorio supervisorRepositorio;
    private PasswordEncoder passwordEncoder;

    public SupervisorServicio(SupervisorRepositorio supervisorRepositorio, PasswordEncoder passwordEncoder) {
        this.supervisorRepositorio = supervisorRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    public Supervisor registrar(Supervisor supervisor){
        supervisor.setPassword(
                passwordEncoder.encode(supervisor.getPassword())
        );

        return supervisorRepositorio.save(supervisor);
    }
}
