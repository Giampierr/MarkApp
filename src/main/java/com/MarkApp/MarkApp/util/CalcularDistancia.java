package com.MarkApp.MarkApp.util;

public final class CalcularDistancia {
    public static double calcularDistanciaMetros(
            double lat1, double lon1,
            double lat2, double lon2) {

        final double RADIO_TIERRA = 6_371_000;

        double latitud1 = Math.toRadians(lat1);
        double latitud2 = Math.toRadians(lat2);

        double diferenciaLatitud =
                Math.toRadians(lat2 - lat1);

        double diferenciaLongitud =
                Math.toRadians(lon2 - lon1);

        double a = Math.pow(Math.sin(diferenciaLatitud / 2), 2)
                + Math.cos(latitud1) * Math.cos(latitud2)
                * Math.pow(Math.sin(diferenciaLongitud / 2), 2);

        double c = 2 * Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1 - a)
        );

        return RADIO_TIERRA * c;
    }
}
