package com.main.libridex.service;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    void init();

    String store(MultipartFile file, int id, String type);

    Stream<Path> loadAll();

    Stream<Path> loadAllUserImages();

    Stream<Path> loadAllBookImages();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void delete(String filename);

    void deleteAll();
}
