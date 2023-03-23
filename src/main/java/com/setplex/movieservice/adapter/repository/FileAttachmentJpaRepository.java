package com.setplex.movieservice.adapter.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.setplex.movieservice.domain.entity.FileAttachment;

public interface FileAttachmentJpaRepository extends FileAttachmentRepository, JpaRepository<FileAttachment, UUID> {

}
