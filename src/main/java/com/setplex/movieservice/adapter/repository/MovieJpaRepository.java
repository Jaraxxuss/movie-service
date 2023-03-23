package com.setplex.movieservice.adapter.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.setplex.movieservice.domain.entity.Movie;

public interface MovieJpaRepository extends MovieRepository, JpaRepository<Movie, UUID> {
    
}
