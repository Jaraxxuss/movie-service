package com.setplex.movieservice.domain.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.setplex.movieservice.adapter.repository.MovieRepository;
import com.setplex.movieservice.domain.entity.Movie;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovieService {
    
    private final MovieRepository movieRepository;

    public Movie getById(UUID movieId) {
        return movieRepository.findById(movieId).orElseThrow(EntityNotFoundException::new);
    }

    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }


}
