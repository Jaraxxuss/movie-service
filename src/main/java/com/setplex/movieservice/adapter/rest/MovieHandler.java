package com.setplex.movieservice.adapter.rest;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.setplex.movieservice.domain.entity.Movie;
import com.setplex.movieservice.domain.service.FileAttachmentService;
import com.setplex.movieservice.domain.service.MovieService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieHandler {

    private final MovieService movieService;
    private final FileAttachmentService fileAttachmentService;

    @GetMapping("{movieId}")
    public ResponseEntity<Movie> getMovie(
        @PathVariable UUID movieId
    ) {
 
        return ResponseEntity.ok(movieService.getById(movieId));
    }
    
    @PostMapping
    public ResponseEntity<Void> createMovie(
        @RequestBody Movie movie
    ) {
        var savedMovie = movieService.save(movie);

        var createdMovieUri = MvcUriComponentsBuilder
            .fromMethodName(MovieHandler.class, "getMovie", savedMovie.getId())
            .buildAndExpand(savedMovie.getId())
            .toUri();

        return ResponseEntity
            .created(createdMovieUri)
            .build();
    }

    @GetMapping("{movieId}/fileAttachments/{fileAttachmentId}")
    public ResponseEntity<byte[]> getFileAttachment(
        @PathVariable UUID movieId,
        @PathVariable UUID fileAttachmentId

    ) throws IOException {
        
        var file = fileAttachmentService
            .getById(fileAttachmentId)
            .getInputStream()
            .readAllBytes();

        return ResponseEntity
            .ok()
            .body(file);
    }

    @PostMapping("{movieId}/fileAttachments")
    public ResponseEntity<Void> createFileAttachment(
        @PathVariable UUID movieId,
        @RequestParam("image") MultipartFile multipartFile
    ) {
 
        var savedFileAttachment = fileAttachmentService.create(movieId, multipartFile);

        var createdFileAttachmentUri = MvcUriComponentsBuilder
            .fromMethodName(MovieHandler.class, "getFileAttachment", movieId, savedFileAttachment.getId())
            .buildAndExpand(movieId, savedFileAttachment.getId())
            .toUri();

        return ResponseEntity
            .created(createdFileAttachmentUri)
            .build();
    }

    @DeleteMapping("{movieId}/fileAttachments/{fileAttachmentId}")
    public ResponseEntity<Void> deleteFileAttachment(
        @PathVariable UUID movieId,
        @PathVariable UUID fileAttachmentId
    ) {
 
        try {
            fileAttachmentService.delete(fileAttachmentId, movieId);
        } catch (EntityNotFoundException e) {
            
        }

        return ResponseEntity
            .noContent()
            .build();
    }
}
