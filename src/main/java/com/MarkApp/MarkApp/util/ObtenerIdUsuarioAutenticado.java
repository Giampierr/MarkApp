package com.MarkApp.MarkApp.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class ObtenerIdUsuarioAutenticado {
    public static int obtenerIdUsuarioAutenticado() {
        Authentication autenticacion =
                SecurityContextHolder.getContext().getAuthentication();

        return Integer.parseInt(autenticacion.getName());
    }
}
