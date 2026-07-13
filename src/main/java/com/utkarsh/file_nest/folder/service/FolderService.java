package com.utkarsh.file_nest.folder.service;

import com.utkarsh.file_nest.entity.Folder;
import com.utkarsh.file_nest.entity.User;
import com.utkarsh.file_nest.folder.dto.CreateFolderRequest;
import com.utkarsh.file_nest.folder.dto.FolderResponse;
import com.utkarsh.file_nest.repository.FolderRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class FolderService {

    private FolderRepository folderRepository;

    public FolderService(FolderRepository folderRepository){
        this.folderRepository = folderRepository;

    }

    public FolderResponse createFolder(CreateFolderRequest request){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Folder parentFolder = null;

        if(request.getParentFolderId() != null ){
            parentFolder = folderRepository.findById(request.getParentFolderId())
                    .orElseThrow(()-> new RuntimeException("Parent Folder not found"));
        }

        Folder folder = new Folder();
        folder.setName(request.getFolderName());
        folder.setOwner(user);

        Folder savedFolder = folderRepository.save(folder);

        Long parentFolderId = savedFolder.getParentFolder()==null ? null : savedFolder.getParentFolder().getId();

        return new FolderResponse(
                savedFolder.getCreatedAt(),
                savedFolder.getId(),
                savedFolder.getName(),
                parentFolderId
                );

    }
}