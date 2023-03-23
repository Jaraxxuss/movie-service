package com.setplex.movieservice.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.setplex.movieservice.adapter.repository.MovieRepository;
import com.setplex.movieservice.domain.entity.Movie;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    private static final UUID MOVIE_ID = UUID.randomUUID();

    @InjectMocks
	private MovieService target;

	@Mock
	private MovieRepository movieRepository;

    @Test
    public void whenGetMovieById_andMovieIsNotPresent__shouldThrowException() {
        // given
        var movieId = MOVIE_ID;
        when(movieRepository.findById(movieId))
            .thenReturn(Optional.empty());

        // when
        Executable result = () -> target.getById(movieId);

        // then
        assertThrows(EntityNotFoundException.class, result);
    }
    
    @Test
    public void whenGetMovieById_andMovieIsPresent__shouldReturnMovie() {
        // given
        var movieId = MOVIE_ID;
        var movie = new Movie();
        movie.setId(movieId);

        when(movieRepository.findById(movieId))
            .thenReturn(Optional.of(movie));

        // when
        var result = target.getById(movieId);

        // then
        assertEquals(movie, result);
    }

    @Test
    public void whenSave_successfullySaved__shouldReturnMovie() {
        // given
        var movieId = MOVIE_ID;
        var movie = new Movie();

        var savedMovie = new Movie();
        savedMovie.setId(movieId);
        
        when(movieRepository.save(movie))
            .thenReturn(savedMovie);

        // when
        var result = target.save(movie);

        // then
        verify(movieRepository).save(movie);
        assertEquals(savedMovie, result);
    }
}
