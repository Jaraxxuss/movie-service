package com.setplex.movieservice.adapter.repository;

import java.util.Optional;
import java.util.UUID;

import com.setplex.movieservice.domain.entity.FileAttachment;

public interface FileAttachmentRepository {
    
    Optional<FileAttachment> findById(UUID fileAttachmentId);

    FileAttachment save(FileAttachment fileAttachment);

    void deleteById(UUID fileAttachmentId);
}
