package com.MarkApp.MarkApp.dto;

public record LoginRespuestaDTO(
        String token,
        String tipoUsuario,
        String rol
) {
}
