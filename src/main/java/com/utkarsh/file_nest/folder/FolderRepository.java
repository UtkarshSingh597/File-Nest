package com.utkarsh.file_nest.folder;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.utkarsh.file_nest.Users.User;

public interface FolderRepository extends JpaRepository<Folders,Long> {

    List<Folders> findByOwner(User owner);

    List<Folders> findByParentFolder(Folders parentFolder);

    Optional<Folders>findByOwnerAndParentFolderAndName(
        User owner,
        Folders parentFolder,
        String name
    );

}
