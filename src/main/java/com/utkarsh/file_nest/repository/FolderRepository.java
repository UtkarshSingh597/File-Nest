package com.utkarsh.file_nest.repository;


import com.utkarsh.file_nest.entity.Folder;
import com.utkarsh.file_nest.entity.User;
import com.utkarsh.file_nest.enums.FolderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface FolderRepository extends JpaRepository<Folder,Long> {

    List<Folder> findByOwnerAndStatus(User owner, FolderStatus status);


    List<Folder> findByParentFolderAndStatus(Folder parentFolder, FolderStatus status);

    Optional<Folder> findByOwnerAndParentFolderAndNameAndStatus(
            User owner,
            Folder parentFolder,
            String name,
            FolderStatus status
    );

    List<Folder> findByParentAndStatus (Folder parentFolder, FolderStatus status);



}