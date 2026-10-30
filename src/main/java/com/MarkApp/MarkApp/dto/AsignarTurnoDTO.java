package com.MarkApp.MarkApp.dto;

import java.time.LocalDate;
import java.util.List;

public record AsignarTurnoDTO(
        int idTurno,
        List<Integer> idColaboradores,
        LocalDate fechaInicio
) {
}
