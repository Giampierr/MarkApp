package com.MarkApp.MarkApp.controladores;

import com.MarkApp.MarkApp.modelo.Supervisor;
import com.MarkApp.MarkApp.repositorio.ColaboradorRepositorio;
import com.MarkApp.MarkApp.repositorio.SupervisorRepositorio;
import com.MarkApp.MarkApp.servicios.ColaboradorServicio;
import com.MarkApp.MarkApp.servicios.SupervisorServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supervisores")
public class SupervisorControlador {
    private final SupervisorRepositorio supervisorRepositorio;
    private final SupervisorServicio supervisorServicio;

    public SupervisorControlador(SupervisorRepositorio supervisorRepositorio, SupervisorServicio supervisorServicio) {
        this.supervisorRepositorio = supervisorRepositorio;
        this.supervisorServicio = supervisorServicio;
    }

    @GetMapping
    public List<Supervisor> listarSuperisores(){
        return supervisorRepositorio.findAll();
    }

    @PostMapping
    public ResponseEntity<Supervisor> añadirSupervisor(
            @RequestBody Supervisor supervisor
    ){
        Supervisor guardado = supervisorServicio.registrar(supervisor);

        return ResponseEntity.status(201).body(guardado);
    }
}
