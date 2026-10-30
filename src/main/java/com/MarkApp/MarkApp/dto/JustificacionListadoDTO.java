
package com.MarkApp.MarkApp.dto;

import com.MarkApp.MarkApp.enums.EstadoJustificacion;
import com.MarkApp.MarkApp.enums.TipoAsistencia;
import java.time.LocalDate;

public record JustificacionListadoDTO(
        int idJustificacion,
        String nombres,
        String apellidos,
        TipoAsistencia tipoAsistencia,
        LocalDate fecha,
        String motivo,
        String evidencia,
        EstadoJustificacion estadoJustificacion
) {}
