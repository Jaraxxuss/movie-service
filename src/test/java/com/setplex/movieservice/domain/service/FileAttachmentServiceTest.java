package com.setplex.movieservice.domain.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.setplex.movieservice.adapter.filestorage.StorageComponent;
import com.setplex.movieservice.adapter.repository.FileAttachmentRepository;
import com.setplex.movieservice.adapter.repository.MovieRepository;
import com.setplex.movieservice.domain.entity.FileAttachment;
import com.setplex.movieservice.domain.entity.Movie;
import com.setplex.movieservice.infrastructure.properties.FoldersProperties;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class FileAttachmentServiceTest {

    private static final String FILE_ATTACHMENTS_FOLDER_NAME = "test";

    private static final UUID MOVIE_ID = UUID.randomUUID();
    private static final UUID FILE_ATTACHMENT_ID = UUID.randomUUID();
    private static final UUID FILE_UUID = UUID.randomUUID();
    private static final MultipartFile MULTIPART_FILE = new MockMultipartFile(
        "image", 
        "image.txt", 
        MediaType.TEXT_PLAIN_VALUE, 
        "image".getBytes()
    );
    
    @InjectMocks
	private FileAttachmentService target;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private FileAttachmentRepository fileAttachmentRepository;
    
    @Mock
    private StorageComponent storageComponent;

    @Mock 
    private FoldersProperties foldersProperties;

    @Captor
    private ArgumentCaptor<FileAttachment> fileAttachmentCaptor;

    @Test
    public void whenGetById__andFileAttachmentIsNotPresent__shouldThrowException() {
        // given
        var fileAttachmentId = FILE_ATTACHMENT_ID;
        
        when(fileAttachmentRepository.findById(fileAttachmentId))
            .thenReturn(Optional.empty());

        // when
        Executable result = () -> target.getById(fileAttachmentId);

        // then
        assertThrows(EntityNotFoundException.class, result);
    }

    // TODO
    // @Test
    public void whenGetById__andFileAttachmentIsPresent__shouldThrowException() {
        // given
        var fileAttachmentId = FILE_ATTACHMENT_ID;
        var fileUUID = FILE_UUID;

        var fileAttachment = new FileAttachment();
        fileAttachment.setId(fileAttachmentId);
        fileAttachment.setFileUUID(fileUUID);
        
        when(fileAttachmentRepository.findById(fileAttachmentId))
            .thenReturn(Optional.of(fileAttachment));

        when(foldersProperties.locations())
            .thenReturn(Map.of(FileAttachmentService.FILE_ATTACHMENTS_FOLDER, new FoldersProperties.FolderProperties(FILE_ATTACHMENTS_FOLDER_NAME)));

        when(storageComponent.loadAsResource(FILE_ATTACHMENTS_FOLDER_NAME, fileUUID))
            .thenReturn(null);

        // when
        var result = target.getById(fileAttachmentId);

        // then
        assertEquals(null, result);
    }

    @Test
    public void whenCreate__andMovietIsNotPresent__shouldThrowException() {
        // given
        var movieId = MOVIE_ID;
        var multipartFile = MULTIPART_FILE;
        
        when(movieRepository.findById(movieId))
            .thenReturn(Optional.empty());

       // when
       Executable result = () -> target.create(movieId, multipartFile);

       // then
       assertThrows(EntityNotFoundException.class, result);
    }

    @Test
    public void whenCreate__andMovietIsPresent__shouldSetFieldsToFIleAttachmentAndSave() {
        // given
        var fileAttachmentId = FILE_ATTACHMENT_ID;
        var fileUUID = FILE_UUID;
        var movieId = MOVIE_ID;
        var multipartFile = MULTIPART_FILE;
        
        var movie = new Movie();
        movie.setId(movieId);

        var fileAttachment = new FileAttachment();
        fileAttachment.setId(fileAttachmentId);

        when(movieRepository.findById(movieId))
            .thenReturn(Optional.of(movie));

        when(foldersProperties.locations())
            .thenReturn(Map.of(FileAttachmentService.FILE_ATTACHMENTS_FOLDER, new FoldersProperties.FolderProperties(FILE_ATTACHMENTS_FOLDER_NAME)));

        when(storageComponent.store(eq(FILE_ATTACHMENTS_FOLDER_NAME), any(UUID.class), eq(multipartFile)))
            .thenReturn(fileUUID);

        when(fileAttachmentRepository.save(fileAttachmentCaptor.capture()))
            .thenReturn(fileAttachment);

       // when
       var result = target.create(movieId, multipartFile);

       // then
       assertEquals(fileAttachmentCaptor.getValue().getFileUUID(), fileUUID);
       assertEquals(fileAttachmentCaptor.getValue().getSize(), multipartFile.getSize());
       assertEquals(fileAttachmentCaptor.getValue().getMovie(), movie);
       assertEquals(fileAttachment, result);
    }

    @Test
    public void whenDelete__andFileAttachmentIsNotPresent__shouldThrowException() {
        // given
        var movieId = MOVIE_ID;
        var fileAttachmentId = FILE_ATTACHMENT_ID;
        
        when(fileAttachmentRepository.findById(fileAttachmentId))
            .thenReturn(Optional.empty());

       // when
       Executable result = () -> target.delete(fileAttachmentId, movieId);

       // then
       assertThrows(EntityNotFoundException.class, result);
    }

    @Test
    public void whenDelete__andFileAttachmentIsPresent__shouldDeleteFromStorageAndRepository() {
        // given
        var movieId = MOVIE_ID;
        var fileAttachmentId = FILE_ATTACHMENT_ID;
        var fileUUID = FILE_UUID;

        var fileAttachment = new FileAttachment();
        fileAttachment.setId(fileAttachmentId);
        fileAttachment.setFileUUID(fileUUID);

        when(fileAttachmentRepository.findById(fileAttachmentId))
            .thenReturn(Optional.of(fileAttachment));

        when(foldersProperties.locations())
            .thenReturn(Map.of(FileAttachmentService.FILE_ATTACHMENTS_FOLDER, new FoldersProperties.FolderProperties(FILE_ATTACHMENTS_FOLDER_NAME)));

       // when
       Executable result = () -> target.delete(fileAttachmentId, movieId);

       // then
       assertDoesNotThrow(result);
       verify(storageComponent).delete(FILE_ATTACHMENTS_FOLDER_NAME, fileUUID);
       verify(fileAttachmentRepository).deleteById(fileAttachmentId);
    }
}
