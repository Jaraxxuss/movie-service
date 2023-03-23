package com.setplex.movieservice.adapter.repository;

import java.util.Optional;
import java.util.UUID;

import com.setplex.movieservice.domain.entity.Movie;

public interface MovieRepository {

    Optional<Movie> findById(UUID movieId);

    Movie save(Movie movie);
}
