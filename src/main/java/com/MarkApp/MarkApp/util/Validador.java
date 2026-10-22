package com.MarkApp.MarkApp.util;

public final class Validador {
    public static boolean validarDni(String dni){
        return dni.matches("\\d{8}");
    }

    public static boolean validarTelefono(String telefono){
        return telefono.matches("9\\d{8}");
    }
}
