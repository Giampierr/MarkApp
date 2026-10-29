
package com.MarkApp.MarkApp.servicios;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class SupabaseStorageServicio {

    private final RestClient restClient;
    private final String bucket;

    public SupabaseStorageServicio(
            @Value("${supabase.url}") String url,
            @Value("${supabase.storage-key}") String storageKey,
            @Value("${supabase.storage-bucket}") String bucket) {

        this.restClient = RestClient.builder()
                .baseUrl(url + "/storage/v1")
                .defaultHeader("apikey", storageKey)
                .defaultHeader("Authorization", "Bearer " + storageKey)
                .build();

        this.bucket = bucket;
    }

    public String subirArchivo(MultipartFile archivo)
            throws IOException {

        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un archivo");
        }

        if (archivo.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "El archivo no puede superar los 5 MB");
        }

        String tipo = archivo.getContentType();

        if (tipo == null ||
                !(tipo.equals("image/jpeg")
                        || tipo.equals("image/png")
                        || tipo.equals("application/pdf"))) {
            throw new IllegalArgumentException(
                    "Tipo de archivo no permitido");
        }

        String extension = switch (tipo) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "application/pdf" -> ".pdf";
            default -> throw new IllegalArgumentException(
                    "Tipo de archivo no permitido");
        };

        String ruta = "evidencias/" + UUID.randomUUID() + extension;

        restClient.post()
                .uri("/object/{bucket}/{ruta}", bucket, ruta)
                .header("x-upsert", "false")
                .contentType(MediaType.parseMediaType(tipo))
                .body(archivo.getBytes())
                .retrieve()
                .toBodilessEntity();

        return ruta;
    }
}
