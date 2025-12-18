package com.citcd.demo.storage;

public record StoredFileInfo(
        String storageKey,
        String originalFilename,
        long sizeBytes,
        String mimeType,
        String sha256) {
}
