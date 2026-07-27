package com.utkarsh.file_nest.File.Service;


import com.utkarsh.file_nest.Exceptions.BadRequest;
import com.utkarsh.file_nest.Exceptions.NoContentException;
import com.utkarsh.file_nest.Exceptions.UnAuthorizedException;
import com.utkarsh.file_nest.File.DTO.FileResponse;
import com.utkarsh.file_nest.auth.service.LoggedUser;
import com.utkarsh.file_nest.entity.File;
import com.utkarsh.file_nest.entity.Folder;
import com.utkarsh.file_nest.entity.User;
import com.utkarsh.file_nest.enums.FileStatus;
import com.utkarsh.file_nest.folder.service.FolderService;
import com.utkarsh.file_nest.repository.FileRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class FileService {
    private static final Logger log = LoggerFactory.getLogger(FileService.class);
    private static final Duration DELETED_FILE_RETENTION = Duration.ofDays(30);
    private final FileRepository fileRepository;
    private final FolderService folderService;
    private final LoggedUser loggedUser;

    public File findOwnedFile(Long fileId) {
        User user = loggedUser.getLoggedUser();

        File file = fileRepository.findById(fileId).orElseThrow(() -> new BadRequest("File Not Found"));

        if (!file.getOwner().getId().equals(user.getId())) {
            throw new UnAuthorizedException("You are not allowed to access this file");
        }
        return file;

    }
        private static final long MAX_FILE_SIZE = 100 * 1024 * 1024;


        private static final Set<String> ALLOWED_MIME_TYPES = new HashSet<>(Set.of(

                "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml",

                "application/pdf", "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.ms-powerpoint",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "text/plain", "text/csv", "text/html",

                "application/zip", "application/x-rar-compressed", "application/x-7z-compressed",
                "application/gzip", "application/x-tar",

                "audio/mpeg", "audio/wav", "audio/ogg", "video/mp4", "video/mpeg", "video/quicktime"
        ));


        private static final Set<String> BLOCKED_EXTENSIONS = new HashSet<>(Set.of(
                ".exe", ".bat", ".cmd", ".com", ".pif", ".scr",
                ".sh", ".bash", ".zsh", ".ksh",
                ".dll", ".sys", ".drv", ".vxd",
                ".dmg", ".pkg", ".deb", ".rpm",
                ".msi", ".msu", ".jar", ".class",
                ".app", ".apk", ".ipa"
        ));

        private String extractExtention (String fileName){
            if (fileName == null || !fileName.contains("."))
                return "";
            return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        }

        private void validateFileType (MultipartFile file, String fileName){
            String mimeType = file.getContentType();
            String extension = extractExtention(fileName);


            if (BLOCKED_EXTENSIONS.contains(extension)) {
                throw new BadRequest("File type not allowed: " + extension);
            }

            if (mimeType == null || mimeType.isBlank()) {
                mimeType = "application/octet-stream";
            }

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

        public FileResponse uploadFile (MultipartFile file, Long folderId){
            if (file == null || file.isEmpty()) {
                throw new BadRequest("File is Empty");
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                throw new BadRequest("File size exceeds maximum limit of 100MB");
            }

            String originalName = file.getOriginalFilename();
            if (originalName == null || originalName.isEmpty()) {
                originalName = "unknow-file";
            }


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
        @Transactional
        public void deleteFile (Long fileId){
         File file = findOwnedFile(fileId);
            if(file.getStatus().equals(FileStatus.DELETED)) {
                throw new NoContentException("File already deleted");
            }

            file.setStatus(FileStatus.DELETED);
            file.setDeletedAt(LocalDateTime.now());
            fileRepository.save(file);

        }

        @Scheduled(cron = "0 0 3 * * *")
        public void purgeExpiredDeletedFiles() {
            purgeDeletedFilesOlderThan(DELETED_FILE_RETENTION);
        }

        @Transactional
        public void purgeDeletedFilesOlderThan(Duration retention) {
            LocalDateTime cutoff = LocalDateTime.now().minus(retention);
            List<File> expiredFiles = fileRepository.findByStatusAndDeletedAtBefore(
                    FileStatus.DELETED, cutoff
            );

            for (File file : expiredFiles) {
                try {
                    Files.deleteIfExists(rootStroage.resolve(file.getStoredName()));
                    fileRepository.delete(file);
                } catch (IOException e) {
                    log.error("Failed to permanently delete stored file {}", file.getStoredName(), e);
                }
            }
        }
    }

