package com.utkarsh.file_nest.File.Service;


import com.utkarsh.file_nest.Exceptions.BadRequest;
import com.utkarsh.file_nest.File.DTO.FileResponse;
import com.utkarsh.file_nest.auth.service.LoggedUser;
import com.utkarsh.file_nest.entity.Folder;
import com.utkarsh.file_nest.entity.User;
import com.utkarsh.file_nest.folder.service.FolderService;
import com.utkarsh.file_nest.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@Service
public class FileService {
    private final FileRepository fileRepository;
    private final FolderService folderService;
    private final LoggedUser loggedUser;

    private final Path rootStroage = Path.of(System.getProperty("user.dir"),"uploads");

    public FileService(FileRepository fileRepository, FolderService folderService, LoggedUser loggedUser) {
        this.fileRepository = fileRepository;
        this.folderService = folderService;
        this.loggedUser = loggedUser;
    }

    public FileResponse uploadFile(MultipartFile file, Long folderId) {
        MultipartFile File = file;
        if (file == null || file.isEmpty()) {
            throw new BadRequest("File is Empty");
        }
        User user = loggedUser.getLoggedUser();

        Folder folder = null;

        if(folder != null){
            folder = folderService.findOwnedFolder(folderId);
        }
        String originalName = file.getOriginalFilename();
        if(originalName==null || originalName.isEmpty()){
            originalName = "unknow-file";
        }

        String mineType = file.getContentType();
        if(mineType == null || mineType.isBlank()){
            mineType = "application/octet-stream";
        }


    }
}
