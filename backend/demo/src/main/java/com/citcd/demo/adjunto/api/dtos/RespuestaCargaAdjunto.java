package com.citcd.demo.adjunto.api.dtos;

public record RespuestaCargaAdjunto(
		String identificadorAlmacenamiento,
		String nombreArchivoOriginal,
		long tamanoBytes,
		String tipoMime,
		String sha256,
		String urlDescarga) {

}
