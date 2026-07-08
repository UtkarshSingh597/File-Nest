package com.utkarsh.file_nest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.utkarsh.file_nest.entity.File;

public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByStoredName(String storedName);

}
