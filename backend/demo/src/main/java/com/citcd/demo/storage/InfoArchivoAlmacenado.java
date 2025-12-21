package com.citcd.demo.storage;

public record InfoArchivoAlmacenado(
                String identificadorAlmacenamiento,
                String nombreOriginal,
                long tamanoBytes,
                String tipoMime,
                String sha256) {
}
