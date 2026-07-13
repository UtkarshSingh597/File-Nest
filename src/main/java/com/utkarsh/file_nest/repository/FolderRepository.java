package com.utkarsh.file_nest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.utkarsh.file_nest.entity.Folder;
import com.utkarsh.file_nest.entity.User;

public interface FolderRepository extends JpaRepository<Folder,Long> {

    List<Folder> findByOwner(User owner);

    List<Folder> findByParentFolder(Folder parentFolder);

    Optional<Folder>findByOwnerAndParentFolderAndName(
        User owner,
        Folder parentFolder,
        String name
    );

}
