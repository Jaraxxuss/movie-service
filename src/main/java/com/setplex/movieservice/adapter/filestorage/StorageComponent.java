package com.setplex.movieservice.adapter.filestorage;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

public interface StorageComponent {
    
    InputStream load(String folder, UUID fileName);

    Resource loadAsResource(String folder, UUID fileName);

    void delete(String folder, UUID fileName);

    UUID store(String folder, UUID fileName, MultipartFile file);
}
