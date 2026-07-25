package com.utkarsh.file_nest.File.Service;


import com.utkarsh.file_nest.Exceptions.BadRequest;
import com.utkarsh.file_nest.File.DTO.FileResponse;
import com.utkarsh.file_nest.auth.service.LoggedUser;
import com.utkarsh.file_nest.entity.Folder;
import com.utkarsh.file_nest.entity.User;
import com.utkarsh.file_nest.enums.FileStatus;
import com.utkarsh.file_nest.folder.service.FolderService;
import com.utkarsh.file_nest.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService {
    private final FileRepository fileRepository;
    private final FolderService folderService;
    private final LoggedUser loggedUser;

    private String extractExtention(String fileName) {
        if (fileName == null || !fileName.contains("."))
            return "";
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private final Path rootStroage = Path.of(System.getProperty("user.dir"), "uploads");

    public FileService(FileRepository fileRepository, FolderService folderService, LoggedUser loggedUser) {
        this.fileRepository = fileRepository;
        this.folderService = folderService;
        this.loggedUser = loggedUser;
    }

    public FileResponse uploadFile(MultipartFile file, Long folderId) {
        if (file == null || file.isEmpty()) {
            throw new BadRequest("File is Empty");
        }
        User user = loggedUser.getLoggedUser();

        Folder folder = null;

        if (folderId != null) {
            folder = folderService.findOwnedFolder(folderId);
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isEmpty()) {
            originalName = "unknow-file";
        }

        String mineType = file.getContentType();
        if (mineType == null || mineType.isBlank()) {
            mineType = "application/octet-stream";
        }

        String storedName = UUID.randomUUID() + extractExtention(originalName);

        try {
            Files.createDirectories(rootStroage);
            Files.copy(
                    file.getInputStream(),
                    rootStroage.resolve(storedName),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store file", e);
        }

        com.utkarsh.file_nest.entity.File fileEntity = new com.utkarsh.file_nest.entity.File();
        fileEntity.setStoredName(storedName);
        fileEntity.setOriginalName(originalName);
        fileEntity.setMimeType(mineType);
        fileEntity.setSize(file.getSize());
        fileEntity.setStatus(FileStatus.UPLOADED);
        fileEntity.setFolder(folder);
        fileEntity.setOwner(user);

        com.utkarsh.file_nest.entity.File savedFile = fileRepository.save(fileEntity);

        return new FileResponse(
                savedFile.getId(),
                savedFile.getOriginalName(),
                savedFile.getSize(),
                savedFile.getFolder() != null ? savedFile.getFolder().getId() : null,
                savedFile.getMimeType(),
                savedFile.getCreatedAt()


        );
    }
}