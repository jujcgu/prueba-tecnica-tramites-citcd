package com.citcd.demo.adjunto.api;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.citcd.demo.adjunto.api.dtos.RespuestaCargaAdjunto;
import com.citcd.demo.storage.InfoArchivoAlmacenado;
import com.citcd.demo.storage.StorageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/adjuntos")
@RequiredArgsConstructor
public class AdjuntoController {

	private final StorageService storageService;

	@PostMapping(value = "/cargar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public RespuestaCargaAdjunto cargar(@RequestPart("file") MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new ResponseStatusException(BAD_REQUEST, "Debe enviar un archivo no vacío");
		}

		InfoArchivoAlmacenado info = storageService.storeAndGetInfo(file);

		String urlDescarga = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/adjuntos/descargar/")
				.path(info.identificadorAlmacenamiento()).toUriString();

		return new RespuestaCargaAdjunto(info.identificadorAlmacenamiento(), info.nombreOriginal(), info.tamanoBytes(),
				info.tipoMime(), info.sha256(), urlDescarga);
	}

	@GetMapping("/descargar/{identificadorAlmacenamiento:.+}")
	public ResponseEntity<Resource> descargar(
			@PathVariable("identificadorAlmacenamiento") String identificadorAlmacenamiento) {
		Resource resource = storageService.loadAsResource(identificadorAlmacenamiento);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

}
