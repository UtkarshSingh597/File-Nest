package com.utkarsh.file_nest.File.Service;

import com.utkarsh.file_nest.File.DTO.FileResponse;
import com.utkarsh.file_nest.auth.service.LoggedUser;
import com.utkarsh.file_nest.entity.User;
import com.utkarsh.file_nest.folder.service.FolderService;
import com.utkarsh.file_nest.repository.FileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileUploadSmokeTest {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private FolderService folderService;

    @Mock
    private LoggedUser loggedUser;

    @Test
    void uploadFileShouldWriteToProjectUploadsFolder() throws Exception {
        FileService fileService = new FileService(fileRepository, folderService, loggedUser);

        User user = new User();
        user.setId(99L);
        user.setName("Smoke Test User");
        user.setEmail("smoke@example.com");
        user.setPassword("secret");
        when(loggedUser.getLoggedUser()).thenReturn(user);

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "smoke-upload.txt",
                "text/plain",
                "hello uploads folder".getBytes()
        );

        LocalDateTime createdAt = LocalDateTime.of(2026, 7, 25, 16, 10);
        when(fileRepository.save(any(com.utkarsh.file_nest.entity.File.class)))
                .thenAnswer(invocation -> {
                    com.utkarsh.file_nest.entity.File entity = invocation.getArgument(0);
                    entity.setId(900L);
                    entity.setCreatedAt(createdAt);
                    return entity;
                });

        FileResponse response = fileService.uploadFile(multipartFile, null);

        ArgumentCaptor<com.utkarsh.file_nest.entity.File> captor = ArgumentCaptor.forClass(com.utkarsh.file_nest.entity.File.class);
        org.mockito.Mockito.verify(fileRepository).save(captor.capture());

        com.utkarsh.file_nest.entity.File savedFile = captor.getValue();
        Path storedPath = Path.of(System.getProperty("user.dir"), "uploads", savedFile.getStoredName());

        assertThat(Files.exists(storedPath)).isTrue();
        assertThat(Files.readString(storedPath)).isEqualTo("hello uploads folder");
        assertThat(response.getId()).isEqualTo(900L);
        assertThat(response.getOriginalName()).isEqualTo("smoke-upload.txt");
        assertThat(response.getMimeType()).isEqualTo("text/plain");
        assertThat(response.getFolderId()).isNull();
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
    }
}


