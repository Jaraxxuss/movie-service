package com.setplex.movieservice.domain.service;

import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.setplex.movieservice.adapter.filestorage.StorageComponent;
import com.setplex.movieservice.adapter.repository.FileAttachmentRepository;
import com.setplex.movieservice.adapter.repository.MovieRepository;
import com.setplex.movieservice.domain.entity.FileAttachment;
import com.setplex.movieservice.infrastructure.properties.FoldersProperties;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileAttachmentService {

    static final String FILE_ATTACHMENTS_FOLDER = "FILE_ATTACHMENTS"; 
    
    private final MovieRepository movieRepository;
    private final FileAttachmentRepository fileAttachmentRepository;
    private final StorageComponent storageComponent;
    private final FoldersProperties foldersProperties;

    public Resource getById(UUID fileAttachmentId) {

        return fileAttachmentRepository
            .findById(fileAttachmentId)
            .map(it -> storageComponent.loadAsResource(getFolderName(), it.getFileUUID()))
            .orElseThrow(EntityNotFoundException::new);
    }

    public FileAttachment create(UUID movieId, MultipartFile multipartFile) {

        return createFileAttachment(movieId, multipartFile);
    }

    public void delete(UUID fileAttachmentId, UUID movieId) {
        
        var fileAttachment = fileAttachmentRepository.findById(fileAttachmentId)
            .orElseThrow(EntityNotFoundException::new);

        storageComponent.delete(getFolderName(), fileAttachment.getFileUUID());
        fileAttachmentRepository.deleteById(fileAttachmentId);
    }

    private FileAttachment createFileAttachment(UUID movieId, MultipartFile multipartFile) {

        var movie = movieRepository
            .findById(movieId)
            .orElseThrow(EntityNotFoundException::new);
        
        var fileAttachment = new FileAttachment();
        fileAttachment.setMovie(movie);
        fileAttachment.setSize(multipartFile.getSize());
        fileAttachment.setFileUUID(storageComponent.store(getFolderName(), UUID.randomUUID(), multipartFile));

        return fileAttachmentRepository.save(fileAttachment);   
    }

    private String getFolderName() {

        return foldersProperties
            .locations()
            .get(FILE_ATTACHMENTS_FOLDER)
            .folder();
    }

}
