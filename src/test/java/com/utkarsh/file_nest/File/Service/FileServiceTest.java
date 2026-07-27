package com.utkarsh.file_nest.File.Service;

import com.utkarsh.file_nest.Exceptions.BadRequest;
import com.utkarsh.file_nest.Exceptions.NoContentException;
import com.utkarsh.file_nest.Exceptions.UnAuthorizedException;
import com.utkarsh.file_nest.File.DTO.FileResponse;
import com.utkarsh.file_nest.auth.service.LoggedUser;
import com.utkarsh.file_nest.entity.Folder;
import com.utkarsh.file_nest.entity.User;
import com.utkarsh.file_nest.enums.FileStatus;
import com.utkarsh.file_nest.folder.service.FolderService;
import com.utkarsh.file_nest.repository.FileRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private FolderService folderService;

    @Mock
    private LoggedUser loggedUser;

    private FileService fileService;
    private String originalUserDir;

    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        originalUserDir = System.getProperty("user.dir");
        tempDir = Files.createTempDirectory("file-service-test");
        System.setProperty("user.dir", tempDir.toString());
        fileService = new FileService(fileRepository, folderService, loggedUser);
    }

    @AfterEach
    void tearDown() {
        if (originalUserDir != null) {
            System.setProperty("user.dir", originalUserDir);
        }
    }

    @Test
    void uploadFileShouldRejectNullFile() {
        assertThrows(BadRequest.class, () -> fileService.uploadFile(null, null));
        verifyNoInteractions(fileRepository, folderService, loggedUser);
    }

    @Test
    void uploadFileShouldRejectEmptyFile() {
        MultipartFile emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);

        assertThrows(BadRequest.class, () -> fileService.uploadFile(emptyFile, null));
        verifyNoInteractions(fileRepository, folderService, loggedUser);
    }

    @Test
    void uploadFileShouldStoreFileAndReturnResponseWithoutFolder() throws Exception {
        User loggedInUser = loggedInUser();
        when(loggedUser.getLoggedUser()).thenReturn(loggedInUser);

        byte[] content = "hello world".getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "notes.txt",
                "text/plain",
                content
        );

        LocalDateTime createdAt = LocalDateTime.of(2026, 7, 25, 10, 30);
        when(fileRepository.save(any(com.utkarsh.file_nest.entity.File.class)))
                .thenAnswer(invocation -> {
                    com.utkarsh.file_nest.entity.File entity = invocation.getArgument(0);
                    entity.setId(101L);
                    entity.setCreatedAt(createdAt);
                    return entity;
                });

        FileResponse response = fileService.uploadFile(multipartFile, null);

        ArgumentCaptor<com.utkarsh.file_nest.entity.File> fileCaptor = ArgumentCaptor.forClass(com.utkarsh.file_nest.entity.File.class);
        verify(fileRepository).save(fileCaptor.capture());
        verify(folderService, never()).findOwnedFolder(anyLong());

        com.utkarsh.file_nest.entity.File savedFile = fileCaptor.getValue();
        assertThat(savedFile.getOwner()).isSameAs(loggedInUser);
        assertThat(savedFile.getFolder()).isNull();
        assertThat(savedFile.getOriginalName()).isEqualTo("notes.txt");
        assertThat(savedFile.getMimeType()).isEqualTo("text/plain");
        assertThat(savedFile.getSize()).isEqualTo(content.length);
        assertThat(savedFile.getStatus()).isEqualTo(FileStatus.UPLOADED);
        assertThat(savedFile.getStoredName()).matches("^[0-9a-fA-F\\-]{36}\\.txt$");

        Path storedPath = tempDir.resolve("uploads").resolve(savedFile.getStoredName());
        assertThat(Files.exists(storedPath)).isTrue();
        assertThat(Files.readAllBytes(storedPath)).isEqualTo(content);

        assertThat(response.getId()).isEqualTo(101L);
        assertThat(response.getOriginalName()).isEqualTo("notes.txt");
        assertThat(response.getSize()).isEqualTo(content.length);
        assertThat(response.getFolderId()).isNull();
        assertThat(response.getMimeType()).isEqualTo("text/plain");
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void uploadFileShouldUseFolderWhenFolderIdProvided() {
        User loggedInUser = loggedInUser();
        when(loggedUser.getLoggedUser()).thenReturn(loggedInUser);

        Folder folder = new Folder();
        folder.setId(55L);
        folder.setOwner(loggedInUser);
        folder.setName("Projects");
        when(folderService.findOwnedFolder(55L)).thenReturn(folder);

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "report.pdf",
                "application/pdf",
                "pdf-content".getBytes()
        );

        LocalDateTime createdAt = LocalDateTime.of(2026, 7, 25, 11, 0);
        when(fileRepository.save(any(com.utkarsh.file_nest.entity.File.class)))
                .thenAnswer(invocation -> {
                    com.utkarsh.file_nest.entity.File entity = invocation.getArgument(0);
                    entity.setId(202L);
                    entity.setCreatedAt(createdAt);
                    return entity;
                });

        FileResponse response = fileService.uploadFile(multipartFile, 55L);

        verify(folderService).findOwnedFolder(55L);
        ArgumentCaptor<com.utkarsh.file_nest.entity.File> fileCaptor = ArgumentCaptor.forClass(com.utkarsh.file_nest.entity.File.class);
        verify(fileRepository).save(fileCaptor.capture());

        com.utkarsh.file_nest.entity.File savedFile = fileCaptor.getValue();
        assertThat(savedFile.getFolder()).isSameAs(folder);
        assertThat(response.getFolderId()).isEqualTo(55L);
        assertThat(response.getId()).isEqualTo(202L);
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void uploadFileShouldFallbackToDefaultMetadataWhenMissing() {
        User loggedInUser = loggedInUser();
        when(loggedUser.getLoggedUser()).thenReturn(loggedInUser);

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                null,
                null,
                "content".getBytes()
        );

        LocalDateTime createdAt = LocalDateTime.of(2026, 7, 25, 12, 0);
        when(fileRepository.save(any(com.utkarsh.file_nest.entity.File.class)))
                .thenAnswer(invocation -> {
                    com.utkarsh.file_nest.entity.File entity = invocation.getArgument(0);
                    entity.setId(303L);
                    entity.setCreatedAt(createdAt);
                    return entity;
                });

        FileResponse response = fileService.uploadFile(multipartFile, null);

        ArgumentCaptor<com.utkarsh.file_nest.entity.File> fileCaptor = ArgumentCaptor.forClass(com.utkarsh.file_nest.entity.File.class);
        verify(fileRepository).save(fileCaptor.capture());

        com.utkarsh.file_nest.entity.File savedFile = fileCaptor.getValue();
        assertThat(savedFile.getOriginalName()).isEqualTo("unknow-file");
        assertThat(savedFile.getMimeType()).isEqualTo("application/octet-stream");
        assertThat(savedFile.getStoredName()).matches("^[0-9a-fA-F\\-]{36}$");
        assertThat(response.getOriginalName()).isEqualTo("unknow-file");
        assertThat(response.getMimeType()).isEqualTo("application/octet-stream");
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void uploadFileShouldWrapIoErrorsInUncheckedIOException() throws Exception {
        User loggedInUser = loggedInUser();
        when(loggedUser.getLoggedUser()).thenReturn(loggedInUser);

        MultipartFile multipartFile = org.mockito.Mockito.mock(MultipartFile.class);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("broken.txt");
        when(multipartFile.getContentType()).thenReturn("text/plain");
        when(multipartFile.getInputStream()).thenThrow(new IOException("disk error"));

        UncheckedIOException exception = assertThrows(
                UncheckedIOException.class,
                () -> fileService.uploadFile(multipartFile, null)
        );

        assertThat(exception).hasMessage("Failed to store file");
        verify(fileRepository, never()).save(any());
    }

    @Test
    void uploadFileShouldRejectFilesExceeding100MB() {
        MultipartFile largeFile = org.mockito.Mockito.mock(MultipartFile.class);
        when(largeFile.isEmpty()).thenReturn(false);
        when(largeFile.getSize()).thenReturn(101 * 1024 * 1024L);

        BadRequest exception = assertThrows(
                BadRequest.class,
                () -> fileService.uploadFile(largeFile, null)
        );

        assertThat(exception.getMessage()).isEqualTo("File size exceeds maximum limit of 100MB");
        verifyNoInteractions(fileRepository, loggedUser);
    }

    @Test
    void uploadFileShouldRejectExecutableFiles() {
        MultipartFile exeFile = new MockMultipartFile(
                "file",
                "malware.exe",
                "application/octet-stream",
                "executable content".getBytes()
        );

        BadRequest exception = assertThrows(
                BadRequest.class,
                () -> fileService.uploadFile(exeFile, null)
        );

        assertThat(exception.getMessage()).contains("File type not allowed");
        verifyNoInteractions(fileRepository, loggedUser);
    }

    @Test
    void uploadFileShouldRejectShellScripts() {
        MultipartFile shFile = new MockMultipartFile(
                "file",
                "script.sh",
                "application/x-sh",
                "#!/bin/bash".getBytes()
        );

        BadRequest exception = assertThrows(
                BadRequest.class,
                () -> fileService.uploadFile(shFile, null)
        );

        assertThat(exception.getMessage()).contains("File type not allowed");
        verifyNoInteractions(fileRepository, loggedUser);
    }

    @Test
    void uploadFileShouldRejectBatFiles() {
        MultipartFile batFile = new MockMultipartFile(
                "file",
                "script.bat",
                "application/x-msdownload",
                "@echo off".getBytes()
        );

        BadRequest exception = assertThrows(
                BadRequest.class,
                () -> fileService.uploadFile(batFile, null)
        );

        assertThat(exception.getMessage()).contains("File type not allowed");
        verifyNoInteractions(fileRepository, loggedUser);
    }

    @Test
    void uploadFileShouldRejectDllFiles() {
        MultipartFile dllFile = new MockMultipartFile(
                "file",
                "library.dll",
                "application/octet-stream",
                "dll content".getBytes()
        );

        BadRequest exception = assertThrows(
                BadRequest.class,
                () -> fileService.uploadFile(dllFile, null)
        );

        assertThat(exception.getMessage()).contains("File type not allowed");
        verifyNoInteractions(fileRepository, loggedUser);
    }

    @Test
    void uploadFileShouldAllowValidImageFiles() {
        User user = loggedInUser();
        when(loggedUser.getLoggedUser()).thenReturn(user);

        MultipartFile imageFile = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                "image content".getBytes()
        );

        var savedFile = new com.utkarsh.file_nest.entity.File();
        savedFile.setId(1L);
        savedFile.setOriginalName("photo.jpg");
        savedFile.setMimeType("image/jpeg");
        savedFile.setSize(imageFile.getSize());
        savedFile.setStatus(FileStatus.UPLOADED);

        when(fileRepository.save(any())).thenReturn(savedFile);

        FileResponse response = fileService.uploadFile(imageFile, null);

        assertThat(response).isNotNull();
        assertThat(response.getOriginalName()).isEqualTo("photo.jpg");
        verify(fileRepository).save(any());
    }

    @Test
    void uploadFileShouldAllowValidPdfFiles() {
        User user = loggedInUser();
        when(loggedUser.getLoggedUser()).thenReturn(user);

        MultipartFile pdfFile = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                "%PDF-1.4".getBytes()
        );

        var savedFile = new com.utkarsh.file_nest.entity.File();
        savedFile.setId(2L);
        savedFile.setOriginalName("document.pdf");
        savedFile.setMimeType("application/pdf");
        savedFile.setSize(pdfFile.getSize());
        savedFile.setStatus(FileStatus.UPLOADED);

        when(fileRepository.save(any())).thenReturn(savedFile);

        FileResponse response = fileService.uploadFile(pdfFile, null);

        assertThat(response).isNotNull();
        assertThat(response.getOriginalName()).isEqualTo("document.pdf");
        verify(fileRepository).save(any());
    }

    @Test
    void uploadFileShouldAllowValidZipFiles() {
        User user = loggedInUser();
        when(loggedUser.getLoggedUser()).thenReturn(user);

        MultipartFile zipFile = new MockMultipartFile(
                "file",
                "archive.zip",
                "application/zip",
                "PK".getBytes()
        );

        var savedFile = new com.utkarsh.file_nest.entity.File();
        savedFile.setId(3L);
        savedFile.setOriginalName("archive.zip");
        savedFile.setMimeType("application/zip");
        savedFile.setSize(zipFile.getSize());
        savedFile.setStatus(FileStatus.UPLOADED);

        when(fileRepository.save(any())).thenReturn(savedFile);

        FileResponse response = fileService.uploadFile(zipFile, null);

        assertThat(response).isNotNull();
        assertThat(response.getOriginalName()).isEqualTo("archive.zip");
        verify(fileRepository).save(any());
    }

    @Test
    void deleteFileShouldMarkOwnedUploadedFileAsDeleted() {
        User user = loggedInUser();
        com.utkarsh.file_nest.entity.File file = new com.utkarsh.file_nest.entity.File();
        file.setId(10L);
        file.setOwner(user);
        file.setStatus(FileStatus.UPLOADED);

        when(loggedUser.getLoggedUser()).thenReturn(user);
        when(fileRepository.findById(10L)).thenReturn(Optional.of(file));

        fileService.deleteFile(10L);

        assertThat(file.getStatus()).isEqualTo(FileStatus.DELETED);
        assertThat(file.getDeletedAt()).isNotNull();
        verify(fileRepository).save(file);
    }

    @Test
    void purgeDeletedFilesOlderThanShouldRemoveStoredFileAndRecord() throws IOException {
        com.utkarsh.file_nest.entity.File file = new com.utkarsh.file_nest.entity.File();
        file.setStoredName("expired-file.txt");

        Path storedFile = tempDir.resolve("uploads").resolve(file.getStoredName());
        Files.createDirectories(storedFile.getParent());
        Files.writeString(storedFile, "expired content");

        when(fileRepository.findByStatusAndDeletedAtBefore(
                org.mockito.ArgumentMatchers.eq(FileStatus.DELETED), any(LocalDateTime.class)
        )).thenReturn(List.of(file));

        fileService.purgeDeletedFilesOlderThan(Duration.ofDays(30));

        assertThat(storedFile).doesNotExist();
        verify(fileRepository).delete(file);
    }

    @Test
    void deleteFileShouldRejectAlreadyDeletedFile() {
        User user = loggedInUser();
        com.utkarsh.file_nest.entity.File file = new com.utkarsh.file_nest.entity.File();
        file.setId(11L);
        file.setOwner(user);
        file.setStatus(FileStatus.DELETED);

        when(loggedUser.getLoggedUser()).thenReturn(user);
        when(fileRepository.findById(11L)).thenReturn(Optional.of(file));

        NoContentException exception = assertThrows(NoContentException.class, () -> fileService.deleteFile(11L));

        assertThat(exception).hasMessage("File already deleted");
        verify(fileRepository, never()).save(any());
    }

    @Test
    void deleteFileShouldRejectFileOwnedByAnotherUser() {
        User loggedInUser = loggedInUser();
        User fileOwner = new User();
        fileOwner.setId(8L);
        com.utkarsh.file_nest.entity.File file = new com.utkarsh.file_nest.entity.File();
        file.setId(12L);
        file.setOwner(fileOwner);
        file.setStatus(FileStatus.UPLOADED);

        when(loggedUser.getLoggedUser()).thenReturn(loggedInUser);
        when(fileRepository.findById(12L)).thenReturn(Optional.of(file));

        UnAuthorizedException exception = assertThrows(
                UnAuthorizedException.class,
                () -> fileService.deleteFile(12L)
        );

        assertThat(exception).hasMessage("You are not allowed to access this file");
        verify(fileRepository, never()).save(any());
    }

    private User loggedInUser() {
        User user = new User();
        user.setId(7L);
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setPassword("secret");
        return user;
    }
}
