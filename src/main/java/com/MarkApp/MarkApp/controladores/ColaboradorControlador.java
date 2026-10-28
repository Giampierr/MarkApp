
package com.MarkApp.MarkApp.controladores;

import com.MarkApp.MarkApp.dto.ColaboradorCambiosDTO;
import com.MarkApp.MarkApp.dto.ColaboradorListadoDTO;
import com.MarkApp.MarkApp.modelo.Colaborador;
import com.MarkApp.MarkApp.repositorio.ColaboradorRepositorio;
import com.MarkApp.MarkApp.servicios.ColaboradorServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colaboradores")
public class ColaboradorControlador {

    private final ColaboradorRepositorio colaboradorRepositorio;
    private final ColaboradorServicio colaboradorServicio;

    public ColaboradorControlador(ColaboradorRepositorio colaboradorRepositorio, ColaboradorServicio colaboradorServicio) {
        this.colaboradorRepositorio = colaboradorRepositorio;
        this.colaboradorServicio = colaboradorServicio;
    }

    @GetMapping("/listado")
    public ResponseEntity<List<ColaboradorListadoDTO>> listarParaAdministracion(){
        return ResponseEntity.ok(colaboradorServicio.listarParaAdministracion());
    }

    @GetMapping("/sin-turno")
    public ResponseEntity<List<Colaborador>> listarSinTurno(){
        return ResponseEntity.ok(colaboradorRepositorio.buscarColaboradoresSinTurno());
    }

    @PostMapping
    public ResponseEntity<Colaborador> añadirColaborador(
            @RequestBody Colaborador colaborador) {

        Colaborador guardado =
                colaboradorServicio.registrar(colaborador);

        return ResponseEntity.status(201).body(guardado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Colaborador> actualizarParcialmente(
            @PathVariable int id,
            @RequestBody ColaboradorCambiosDTO cambios
            ){

        Colaborador actualizado = colaboradorServicio.actualizarParcialmente(id, cambios);

        return ResponseEntity.ok(actualizado);
    }


}
