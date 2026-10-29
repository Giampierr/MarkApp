package com.MarkApp.MarkApp.dto;

import com.MarkApp.MarkApp.enums.EstadoJustificacion;


public record JustificacionAtendidaDTO(
        int idJustificacion,
        String respuestaSupervisor,
        EstadoJustificacion estadoJustificacion
) {

}
