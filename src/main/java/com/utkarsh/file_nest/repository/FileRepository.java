package com.utkarsh.file_nest.repository;

import com.utkarsh.file_nest.entity.File;
import com.utkarsh.file_nest.entity.Folder;
import com.utkarsh.file_nest.enums.FileStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByStoredName(String storedName);

     List<File> findByFolderAndStatusNot(Folder folder, FileStatus status);

}
