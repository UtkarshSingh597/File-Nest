package com.utkarsh.file_nest.repository;


import com.utkarsh.file_nest.entity.Folder;
import com.utkarsh.file_nest.entity.User;
import com.utkarsh.file_nest.enums.FolderStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;


public interface FolderRepository extends JpaRepository<Folder,Long> {

    List<Folder> findByOwnerAndStatus(User owner, FolderStatus status);


    List<Folder> findByParentFolderAndStatus(Folder parentFolder, FolderStatus status);

    Optional<Folder> findByOwnerAndParentFolderAndNameAndStatus(
            User owner,
            Folder parentFolder,
            String name,
            FolderStatus status
    );

    @Query(value = """
            WITH RECURSIVE folder_tree AS (
                SELECT id FROM folders WHERE id = :folderId AND status = 'ACTIVE'
                UNION ALL
                SELECT child.id
                FROM folders child
                INNER JOIN folder_tree parent ON child.parent_folder_id = parent.id
                WHERE child.status = 'ACTIVE'
            )
            SELECT id FROM folder_tree
            """, nativeQuery = true)
    List<Long> findActiveSubtreeIds(@Param("folderId") Long folderId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Folder folder
            set folder.status = :status, folder.deletedAt = :deletedAt
            where folder.id in :folderIds
            """)
    int softDeleteByIdIn(
            @Param("folderIds") List<Long> folderIds,
            @Param("status") FolderStatus status,
            @Param("deletedAt") LocalDateTime deletedAt
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            delete from Folder folder
            where folder.status = :status
              and folder.deletedAt < :cutoff
              and folder.files is empty
              and folder.subFolders is empty
            """)
    int deleteExpiredLeafFolders(
            @Param("status") FolderStatus status,
            @Param("cutoff") LocalDateTime cutoff
    );



}
