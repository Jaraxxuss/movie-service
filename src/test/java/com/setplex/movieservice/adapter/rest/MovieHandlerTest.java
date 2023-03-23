package com.setplex.movieservice.adapter.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.setplex.movieservice.domain.entity.FileAttachment;
import com.setplex.movieservice.domain.entity.Movie;
import com.setplex.movieservice.domain.service.FileAttachmentService;
import com.setplex.movieservice.domain.service.MovieService;

import jakarta.persistence.EntityNotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

@WebMvcTest(MovieHandler.class)
public class MovieHandlerTest {

    private static final UUID MOVIE_ID = UUID.randomUUID();
    private static final UUID FILE_ATTACHMENT_ID = UUID.randomUUID();
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
	private MovieService movieService;

    @MockBean
	private FileAttachmentService fileAttachmentService;

    @Value("classpath:requests/createMovie.json")
    private Resource createMovie;

    @Value("classpath:response/getFileAttachmentById.png")
    private Resource getFileAttachmentById;

    @Test
    public void whenGetMovieById__andMovieIsNotPresent__shouldReturn404status() throws Exception {
        // given
        var movieId = MOVIE_ID;
        when(movieService.getById(movieId))
            .thenThrow(EntityNotFoundException.class);
        
        // when
        this.mockMvc
            .perform(get("/movies/{movieId}", movieId))
        // then
            .andExpect(status().is4xxClientError());

    }

    @Test
    public void whenGetMovieById__andMovieIsPresent__shouldReturnMovieAndOkStatus() throws Exception {
        // given
        var movieId = MOVIE_ID;
        var movie = new Movie();
        movie.setId(movieId);
        when(movieService.getById(movieId))
            .thenReturn(movie);
        
        // when
        this.mockMvc
            .perform(get("/movies/{movieId}", movieId))
        // then
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void whenCreateMovie__succesfullyCreated__shouldReturnCreatedStatusAndMovieLocation() throws Exception {        
        // given
        var movieId = MOVIE_ID;
        var movie = new Movie();
        movie.setId(movieId);
        when(movieService.save(any(Movie.class)))
            .thenReturn(movie);
        
        // when
        this.mockMvc
            .perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createMovie.getContentAsByteArray())
            )
        // then
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, containsString("/movies/%s".formatted(movieId))));
    }

    @Test
    public void whenGetFileAttachmentById__andFileAttachmentIsNotPresent__shouldReturn404status() throws Exception {
        // given
        var movieId = MOVIE_ID;
        var fileAttachmentId = FILE_ATTACHMENT_ID;

        when(fileAttachmentService.getById(fileAttachmentId))
            .thenThrow(EntityNotFoundException.class);
        
        // when
        this.mockMvc
            .perform(get("/movies/{movieId}/fileAttachments/{fileAttachmentId}", movieId, fileAttachmentId))
        // then
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void whenGetFileAttachmentById__andFileAttachmentIsPresent__shouldReturnFileAttachmentAndOkStatus() throws Exception {
        // given
        var movieId = MOVIE_ID;
        var fileAttachmentId = FILE_ATTACHMENT_ID;
        var fileAttachment = new FileAttachment();
        fileAttachment.setId(fileAttachmentId);

        when(fileAttachmentService.getById(fileAttachmentId))
            .thenReturn(getFileAttachmentById);
        
        // when
        this.mockMvc
            .perform(get("/movies/{movieId}/fileAttachments/{fileAttachmentId}", movieId, fileAttachmentId))
        // then
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    public void whenCreateFileAttachment__succesfullyCreated__shouldReturnCreatedStatusAndFileAttachmentLocation() throws Exception {        
        // given
        var movieId = MOVIE_ID;
        var fileAttachmentId = FILE_ATTACHMENT_ID;
        var fileAttachment = new FileAttachment();
        fileAttachment.setId(fileAttachmentId);

        var file = new MockMultipartFile(
            "image", 
            "image.txt", 
            MediaType.TEXT_PLAIN_VALUE, 
            "image".getBytes()
        );

        when(fileAttachmentService.create(movieId, file))
            .thenReturn(fileAttachment);
        
        // when
        this.mockMvc
            .perform(multipart("/movies/{movieId}/fileAttachments", movieId)
                .file(file)
            )
        // then
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, containsString("/movies/%s/fileAttachments/%s".formatted(movieId, fileAttachmentId))));
    }

    @Test
    public void whenDeleteFileAttachment__succesfullyDeleted__shouldReturnNoContentStatus() throws Exception {        
        // given
        var movieId = MOVIE_ID;
        var fileAttachmentId = FILE_ATTACHMENT_ID;

        doNothing()
            .when(fileAttachmentService)
            .delete(fileAttachmentId, movieId);
        
        // when
        this.mockMvc
            .perform(delete("/movies/{movieId}/fileAttachments/{fileAttachmentId}", movieId, fileAttachmentId))
        // then
            .andExpect(status().isNoContent());
    }

    @Test
    public void whenDeleteFileAttachment__andFileAttachmentIsNotPresent__shouldReturnNoContentStatus() throws Exception {        
        // given
        var movieId = MOVIE_ID;
        var fileAttachmentId = FILE_ATTACHMENT_ID;

        doThrow(EntityNotFoundException.class)
            .when(fileAttachmentService)
            .delete(fileAttachmentId, movieId);
        
        // when
        this.mockMvc
            .perform(delete("/movies/{movieId}/fileAttachments/{fileAttachmentId}", movieId, fileAttachmentId))
        // then
            .andExpect(status().isNoContent());
    }
}
