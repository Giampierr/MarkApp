
package com.MarkApp.MarkApp.controladores;

import com.MarkApp.MarkApp.modelo.DetalleRefrigerio;
import com.MarkApp.MarkApp.servicios.DetalleRefrigerioAsistencia;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/refrigerios")
public class DetalleRefrigerioControlador {

    private final DetalleRefrigerioAsistencia refrigerioServicio;

    public DetalleRefrigerioControlador(
            DetalleRefrigerioAsistencia refrigerioServicio) {
        this.refrigerioServicio = refrigerioServicio;
    }

    @PostMapping("/salida/{idColaborador}")
    public ResponseEntity<DetalleRefrigerio> registrarSalida(
            @PathVariable int idColaborador) {

        DetalleRefrigerio refrigerio =
                refrigerioServicio.registrarSalidaRefrigerio(
                        idColaborador
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(refrigerio);
    }

    @PatchMapping("/regreso/{idColaborador}")
    public ResponseEntity<DetalleRefrigerio> registrarRegreso(
            @PathVariable int idColaborador) {

        DetalleRefrigerio refrigerio =
                refrigerioServicio.registrarRegresoRefrigerio(
                        idColaborador
                );

        return ResponseEntity.ok(refrigerio);
    }
}
