package com.MarkApp.MarkApp.controladores;

import com.MarkApp.MarkApp.modelo.Colaborador;
import com.MarkApp.MarkApp.repositorio.ColaboradorRepositorio;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("")
public class ColaboradorControlador {
    private final ColaboradorRepositorio colaboradorRepositorio;

    public ColaboradorControlador(ColaboradorRepositorio colaboradorRepositorio) {
        this.colaboradorRepositorio = colaboradorRepositorio;
    }


    @GetMapping
    public List<Colaborador> listarColaboradores(){
        return colaboradorRepositorio.findAll();
    }

    @PostMapping
    public void añadirColaborador(Colaborador colaborador){
        colaboradorRepositorio.save(colaborador);
    }
}
