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

import com.citcd.demo.storage.StorageService;
import com.citcd.demo.storage.StoredFileInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/adjuntos")
@RequiredArgsConstructor
public class AdjuntoUploadController {

    private final StorageService storageService;

    public record UploadAdjuntoResponse(
            String storageKey,
            String originalFilename,
            long sizeBytes,
            String mimeType,
            String sha256,
            String downloadUrl) {
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadAdjuntoResponse upload(@RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Debe enviar un archivo no vacío");
        }

        StoredFileInfo info = storageService.storeAndGetInfo(file);

        String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/adjuntos/files/")
                .path(info.storageKey())
                .toUriString();

        return new UploadAdjuntoResponse(
                info.storageKey(),
                info.originalFilename(),
                info.sizeBytes(),
                info.mimeType(),
                info.sha256(),
                downloadUrl);
    }

    @GetMapping("/files/{storageKey:.+}")
    public ResponseEntity<Resource> download(@PathVariable String storageKey) {
        Resource resource = storageService.loadAsResource(storageKey);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
