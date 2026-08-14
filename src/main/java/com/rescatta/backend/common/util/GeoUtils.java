package com.rescatta.backend.common.util;

/**
 * Cálculo de distancia entre dos coordenadas con la fórmula de Haversine.
 *
 * Nota de arquitectura: para un dataset grande, este filtro "cerca de mí" debería
 * resolverse en la base de datos con una extensión geoespacial (PostGIS + ST_DWithin),
 * no en memoria en la capa de servicio. Se deja documentado como mejora futura; por ahora,
 * con el volumen esperado para el proyecto de curso, calcular la distancia en memoria
 * sobre el resultado ya filtrado por especie/estado es suficiente y mucho más simple.
 */
public final class GeoUtils {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private GeoUtils() {
    }

    public static double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}
