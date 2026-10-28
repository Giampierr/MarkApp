package com.MarkApp.MarkApp.controlador;

import com.MarkApp.MarkApp.dto.TurnoDTO;
import com.MarkApp.MarkApp.modelo.Turno;
import com.MarkApp.MarkApp.servicios.TurnoServicio;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/turnos")
public class TurnoControlador {

    private final TurnoServicio turnoServicio;

    public TurnoControlador(TurnoServicio turnoServicio) {
        this.turnoServicio = turnoServicio;
    }

    @PostMapping
    public ResponseEntity<Turno> crearTurno(
            @Valid @RequestBody TurnoDTO turnoDTO) {

        Turno turnoCreado = turnoServicio.crearTurno(turnoDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(turnoCreado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Turno> editarTurno(@PathVariable int id,
            @Valid @RequestBody TurnoDTO turnoDTO) {

        Turno actualizado = turnoServicio.editarTurno(id,turnoDTO);

        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTurno(@PathVariable int id){
        turnoServicio.eliminarTurno(id);

        return ResponseEntity.noContent().build();
    }
}