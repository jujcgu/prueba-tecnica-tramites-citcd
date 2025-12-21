package com.citcd.demo.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void store(MultipartFile file);

    InfoArchivoAlmacenado storeAndGetInfo(MultipartFile file);

    InfoArchivoAlmacenado getInfo(String identificadorAlmacenamiento);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();
}
