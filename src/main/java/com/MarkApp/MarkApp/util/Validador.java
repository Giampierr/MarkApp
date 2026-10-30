package com.MarkApp.MarkApp.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class Validador {
    public static boolean validarDni(String dni){
        return dni.matches("\\d{8}");
    }

    public static boolean validarTelefono(String telefono){
        return telefono.matches("9\\d{8}");
    }

    public static void validarCoordenadas(double latitud, double longitud) {
        if (!Double.isFinite(latitud) || !Double.isFinite(longitud)
                || latitud < -90 || latitud > 90
                || longitud < -180 || longitud > 180) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Las coordenadas GPS no son válidas"
            );
        }
    }
}
