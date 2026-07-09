package com.utkarsh.file_nest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.utkarsh.file_nest.entity.Folders;
import com.utkarsh.file_nest.entity.User;

public interface FolderRepository extends JpaRepository<Folders,Long> {

    List<Folders> findByOwner(User owner);

    List<Folders> findByParentFolder(Folders parentFolder);

    Optional<Folders>findByOwnerAndParentFolderAndName(
        User owner,
        Folders parentFolder,
        String name
    );

}
