package com.setplex.movieservice.adapter.filestorage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class InMemoryStorageComponent implements StorageComponent {

    @Override
    public InputStream load(String folder, UUID fileName) {
        try {
            return new FileInputStream(Paths.get(folder).resolve(fileName.toString()).toFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No such file or directory : %s".formatted(fileName.toString()), e);
        }
    }

    @Override
    public void delete(String folder, UUID fileName) {
        try {
            FileSystemUtils.deleteRecursively(Paths.get(folder).resolve(fileName.toString()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file %s".formatted(fileName.toString()));
        }
    }

    @Override
    public UUID store(String folder, UUID fileName, MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file %s".formatted(fileName.toString()));
        }
        Paths.get(folder).toFile().mkdirs();
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, Paths.get(folder).resolve(fileName.toString()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file %s at %s}".formatted(fileName.toString(), Paths.get(folder).toFile().toString()), e);
        }

        return fileName;
    }

    @Override
    public Resource loadAsResource(String folder, UUID fileName) {
        try (InputStream inputStream = load(folder, fileName)) {
            var resource = new ByteArrayResource(inputStream.readAllBytes());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: %s".formatted(fileName.toString()));
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not read file: ${param.fileName}", e);
        }
    }
    
}
