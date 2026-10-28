package com.MarkApp.MarkApp.dto;

import com.MarkApp.MarkApp.enums.DiaSemana;
import jakarta.validation.constraints.*;
import java.time.LocalTime;
import java.util.List;

public record TurnoDTO(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotNull(message = "La hora de ingreso es obligatoria")
        LocalTime horaIngreso,

        @NotNull(message = "El inicio del refrigerio es obligatorio")
        LocalTime inicioRefrigerio,

        @NotNull(message = "El fin del refrigerio es obligatorio")
        LocalTime finRefrigerio,

        @NotNull(message = "La hora de salida es obligatoria")
        LocalTime horaSalida,

        @PositiveOrZero(message = "La tolerancia no puede ser negativa")
        int minTolerancia,

        @NotEmpty(message = "Debe seleccionar al menos un día")
        List<@NotNull DiaSemana> diasSemana

) {}