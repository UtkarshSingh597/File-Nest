package com.utkarsh.file_nest.File.Service;

import com.utkarsh.file_nest.Exceptions.BadRequest;
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
import java.time.LocalDateTime;

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

    private User loggedInUser() {
        User user = new User();
        user.setId(7L);
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setPassword("secret");
        return user;
    }
}



