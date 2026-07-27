package com.utkarsh.file_nest.repository;

import com.utkarsh.file_nest.entity.File;
import com.utkarsh.file_nest.entity.Folder;
import com.utkarsh.file_nest.enums.FileStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByStoredName(String storedName);

     List<File> findByFolderAndStatusNot(Folder folder, FileStatus status);

     List<File> findByStatusAndDeletedAtBefore(FileStatus status, LocalDateTime cutoff);

     @Modifying(clearAutomatically = true, flushAutomatically = true)
     @Query("""
             update File file
             set file.status = :status, file.deletedAt = :deletedAt
             where file.folder.id in :folderIds and file.status <> :status
             """)
     int softDeleteByFolderIdIn(
             @Param("folderIds") List<Long> folderIds,
             @Param("status") FileStatus status,
             @Param("deletedAt") LocalDateTime deletedAt
     );

}
