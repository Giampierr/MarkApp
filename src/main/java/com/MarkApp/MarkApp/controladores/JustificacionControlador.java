
package com.MarkApp.MarkApp.controladores;

import com.MarkApp.MarkApp.dto.JustificacionAtendidaDTO;
import com.MarkApp.MarkApp.dto.JustificacionListadoDTO;
import com.MarkApp.MarkApp.enums.TipoAsistencia;
import com.MarkApp.MarkApp.modelo.Justificacion;
import com.MarkApp.MarkApp.repositorio.JustificacionRepositorio;
import com.MarkApp.MarkApp.servicios.JustificacionesServicio;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/justificacion")
public class JustificacionControlador {

    private final JustificacionRepositorio justificacionRepositorio;
    private final JustificacionesServicio justificacionesServicio;

    public JustificacionControlador(
            JustificacionRepositorio justificacionRepositorio,
            JustificacionesServicio justificacionesServicio) {
        this.justificacionRepositorio = justificacionRepositorio;
        this.justificacionesServicio = justificacionesServicio;
    }

    @PostMapping(
            value = "/colaboradores/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Justificacion> agregarJustificacion(
            @PathVariable int id,
            @RequestParam TipoAsistencia tipoAsistencia,
            @RequestParam String mensajeColaborador,
            @RequestPart(required = false) MultipartFile archivo
    ) throws IOException {

        Justificacion creada =
                justificacionesServicio.agregarJustificacion(
                        id,
                        tipoAsistencia,
                        mensajeColaborador,
                        archivo
                );

        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PatchMapping("/supervisores/{idSupervisor}")
    public ResponseEntity<Justificacion> atenderJustificacion(
            @PathVariable int idSupervisor,
            @RequestBody JustificacionAtendidaDTO justificacionDTO) {

        Justificacion atendida =
                justificacionesServicio.atenderJustificacion(
                        idSupervisor,
                        justificacionDTO
                );

        return ResponseEntity.ok(atendida);
    }


    @GetMapping("/listado")
    public ResponseEntity<List<JustificacionListadoDTO>> listarParaAdministracion() {

        return ResponseEntity.ok(
                justificacionesServicio.listarParaAdministracion()
        );
    }
}
