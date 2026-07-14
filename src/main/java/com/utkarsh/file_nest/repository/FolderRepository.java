package com.utkarsh.file_nest.repository;

import com.utkarsh.file_nest.entity.Folder;
import com.utkarsh.file_nest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder,Long> {

    List<Folder> findByOwner(User owner);

    List<Folder> findByParentFolder(Folder parentFolder);

    Optional<Folder> findByOwnerAndParentFolderAndName(
            User owner,
            Folder parentFolder,
            String name
    );

}