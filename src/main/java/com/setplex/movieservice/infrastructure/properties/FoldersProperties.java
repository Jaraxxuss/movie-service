package com.setplex.movieservice.infrastructure.properties;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "folders")
public record FoldersProperties(Map<String, FolderProperties> locations) {
    
    public record FolderProperties(String folder) {
    }
}
