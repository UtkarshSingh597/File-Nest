package com.utkarsh.file_nest.folder.service;

import com.utkarsh.file_nest.Exceptions.FolderAccessDenailedException;
import com.utkarsh.file_nest.Exceptions.FolderAlreadyExistsException;
import com.utkarsh.file_nest.Exceptions.FolderNotFoundException;
import com.utkarsh.file_nest.auth.service.LoggedUser;
import com.utkarsh.file_nest.entity.Folder;
import com.utkarsh.file_nest.entity.User;
import com.utkarsh.file_nest.folder.dto.CreateFolderRequest;
import com.utkarsh.file_nest.folder.dto.FolderResponse;
import com.utkarsh.file_nest.folder.dto.RenameFolderRequest;
import com.utkarsh.file_nest.repository.FolderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FolderService {

    private FolderRepository folderRepository;
    private LoggedUser loggedUser;

    public FolderService(FolderRepository folderRepository, LoggedUser loggedUser){
        this.folderRepository = folderRepository;
        this.loggedUser= loggedUser;

    }

    private FolderResponse mapToFolderResponse(Folder folder){
        Long parentFolderId = folder.getParentFolder()==null
                ? null
                : folder.getParentFolder().getId();

        return new FolderResponse(
                folder.getCreatedAt(),
                folder.getId(),
                folder.getName(),
                parentFolderId
        );
    }
private Folder folderVerification(Long folderId){
        Folder folder = folderRepository.findById(folderId).orElseThrow(()-> new FolderNotFoundException("Folder Not Found"));
    if(!folder.getOwner().getId().equals(loggedUser.getLoggedUser().getId())){
        throw new FolderAccessDenailedException("Not Authorized to Access this Folder");
    }
    return folder;
}


    public FolderResponse createFolder(CreateFolderRequest request){
        User user = loggedUser.getLoggedUser();

        Folder parentFolder = null;

        if(request.getParentFolderId() != null ){
            parentFolder = folderRepository.findById(request.getParentFolderId())
                    .orElseThrow(()-> new RuntimeException("Parent Folder not found"));
        }

        Folder folder = new Folder();
        folder.setName(request.getFolderName());
        folder.setOwner(user);

        Folder savedFolder = folderRepository.save(folder);

        return mapToFolderResponse(savedFolder);

    }


    public FolderResponse getFolder(Long folderId) {


        Folder folder = folderVerification(folderId);


       return mapToFolderResponse(folder);
    }

    public List<FolderResponse> getAllFolders() {
User user = loggedUser.getLoggedUser();

List<Folder> folders = folderRepository.findByOwner(user);

return folders.stream().map(this::mapToFolderResponse).toList();
    }


    public FolderResponse renameFolder(Long folderId, RenameFolderRequest request){

        Folder folder = folderVerification(folderId);

        Optional<Folder> exisitingFolder = folderRepository.findByOwnerAndParentFolderAndName(folder.getOwner(),folder.getParentFolder(), request.getFolderName());
        if(exisitingFolder.isPresent() && exisitingFolder.get().getId()!=folder.getId()){
            throw new FolderAlreadyExistsException("Folder Already Exists");
        }
folder.setName(request.getFolderName());
        Folder updatedFolder = folderRepository.save(folder);

        return mapToFolderResponse(updatedFolder);


    }
}