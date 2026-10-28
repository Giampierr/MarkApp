
package com.MarkApp.MarkApp.dto;

import com.MarkApp.MarkApp.enums.EstadoColaborador;
import com.MarkApp.MarkApp.enums.Jornada;

public record ColaboradorListadoDTO(
        Integer id,
        String dni,
        String nombres,
        String apellidos,
        String correo,
        String cargo,
        Jornada jornada,
        EstadoColaborador estadoColaborador,
        String nombreDispositivo,
        Boolean dispositivoActivo
) {}
