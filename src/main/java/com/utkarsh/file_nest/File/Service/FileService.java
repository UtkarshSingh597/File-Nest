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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class FileService {
    private final FileRepository fileRepository;
    private final FolderService folderService;
    private final LoggedUser loggedUser;

    // File size limit: 100MB
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024;

    // Allowed MIME types (whitelist approach)
    private static final Set<String> ALLOWED_MIME_TYPES = new HashSet<>(Set.of(
            // Images
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml",
            // Documents
            "application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "text/plain", "text/csv", "text/html",
            // Archives
            "application/zip", "application/x-rar-compressed", "application/x-7z-compressed",
            "application/gzip", "application/x-tar",
            // Media
            "audio/mpeg", "audio/wav", "audio/ogg", "video/mp4", "video/mpeg", "video/quicktime"
    ));

    // Blocked file extensions (dangerous executables)
    private static final Set<String> BLOCKED_EXTENSIONS = new HashSet<>(Set.of(
            ".exe", ".bat", ".cmd", ".com", ".pif", ".scr",
            ".sh", ".bash", ".zsh", ".ksh",
            ".dll", ".sys", ".drv", ".vxd",
            ".dmg", ".pkg", ".deb", ".rpm",
            ".msi", ".msu", ".jar", ".class",
            ".app", ".apk", ".ipa"
    ));

    private String extractExtention(String fileName) {
        if (fileName == null || !fileName.contains("."))
            return "";
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }

    private void validateFileType(MultipartFile file, String fileName) {
        String mimeType = file.getContentType();
        String extension = extractExtention(fileName);

        // Check if extension is blocked
        if (BLOCKED_EXTENSIONS.contains(extension)) {
            throw new BadRequest("File type not allowed: " + extension);
        }

        // Validate MIME type against whitelist
        if (mimeType == null || mimeType.isBlank()) {
            mimeType = "application/octet-stream";
        }

        // Check if MIME type is allowed (unless it's octet-stream, which is default for unknown)
        if (!ALLOWED_MIME_TYPES.contains(mimeType) && !mimeType.equals("application/octet-stream")) {
            throw new BadRequest("File type not allowed: " + mimeType);
        }
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

        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequest("File size exceeds maximum limit of 100MB");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isEmpty()) {
            originalName = "unknow-file";
        }

        // Validate file type (MIME type and extension)
        validateFileType(file, originalName);

        User user = loggedUser.getLoggedUser();

        Folder folder = null;

        if (folderId != null) {
            folder = folderService.findOwnedFolder(folderId);
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