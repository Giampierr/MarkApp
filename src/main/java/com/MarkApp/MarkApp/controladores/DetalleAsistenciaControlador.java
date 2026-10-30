package com.MarkApp.MarkApp.controladores;

import com.MarkApp.MarkApp.dto.UbicacionRequestDto;
import com.MarkApp.MarkApp.modelo.DetalleAsistencia;
import com.MarkApp.MarkApp.servicios.DetalleAsistenciaServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asistencias")
public class DetalleAsistenciaControlador {
    private final DetalleAsistenciaServicio asistenciaServicio;

    public DetalleAsistenciaControlador(DetalleAsistenciaServicio asistenciaServicio) {
        this.asistenciaServicio = asistenciaServicio;
    }

    @PostMapping("/ingreso/{idColaborador}")
    public ResponseEntity<DetalleAsistencia> registrarAsistencia(@PathVariable int idColaborador, @RequestBody UbicacionRequestDto ubicacion){
        DetalleAsistencia asistencia = asistenciaServicio.registrarIngreso(idColaborador, ubicacion.latitud(), ubicacion.longitud());

        return ResponseEntity.status(HttpStatus.CREATED).body(asistencia);
    }

    @PatchMapping("/salida/{idColaborador}")
    public ResponseEntity<DetalleAsistencia> registrarSalida(@PathVariable int idColaborador,@RequestBody UbicacionRequestDto ubicacion){
        DetalleAsistencia asistencia = asistenciaServicio.registrarSalida(idColaborador, ubicacion.latitud(), ubicacion.longitud());

        return ResponseEntity.ok(asistencia);
    }
}
