package com.MarkApp.MarkApp.dto;

import com.MarkApp.MarkApp.enums.TipoAsistencia;
import java.time.LocalDateTime;

public record JustificacionDTO(
        TipoAsistencia tipoAsistencia,
        String mensajeColaborador,
        String archivoColaborador
) {
}
