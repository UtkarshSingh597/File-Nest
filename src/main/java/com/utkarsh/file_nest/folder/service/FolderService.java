package com.utkarsh.file_nest.folder.service;

import com.utkarsh.file_nest.Exceptions.FolderAccessDenailedException;
import com.utkarsh.file_nest.Exceptions.FolderAlreadyExistsException;
import com.utkarsh.file_nest.Exceptions.FolderNotFoundException;
import com.utkarsh.file_nest.auth.service.LoggedUser;
import com.utkarsh.file_nest.entity.File;
import com.utkarsh.file_nest.entity.Folder;
import com.utkarsh.file_nest.entity.User;
import com.utkarsh.file_nest.enums.FileStatus;
import com.utkarsh.file_nest.enums.FolderStatus;
import com.utkarsh.file_nest.folder.dto.CreateFolderRequest;
import com.utkarsh.file_nest.folder.dto.FolderResponse;
import com.utkarsh.file_nest.folder.dto.RenameFolderRequest;
import com.utkarsh.file_nest.repository.FolderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.utkarsh.file_nest.repository.FileRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FolderService {

    private FileRepository fileRepository;
    private FolderRepository folderRepository;
    private LoggedUser loggedUser;

    public FolderService(FolderRepository folderRepository, LoggedUser loggedUser, FileRepository fileRepository){
        this.folderRepository = folderRepository;
        this.loggedUser= loggedUser;
        this.fileRepository = fileRepository;
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

private Folder findOwnedFolder(Long folderId){
        Folder folder = folderRepository.findById(folderId).orElseThrow(()-> new FolderNotFoundException("Folder Not Found"));
    if(folder.getStatus()==(FolderStatus.DELETED)){
        throw new FolderNotFoundException("This Folder was Deleted");
    }
    if(!folder.getOwner().getId().equals(loggedUser.getLoggedUser().getId())){
        throw new FolderAccessDenailedException("Not Authorized to Access this Folder");
    }

    return folder;
}


    private void deleteFolderRecursively(Folder folder){


        List<Folder>childFolders = folderRepository.findByParentFolderAndStatus(
                folder,
                FolderStatus.ACTIVE
        );

        for(Folder childFolder : childFolders){
            deleteFolderRecursively(childFolder);
        }

        List<File>files = fileRepository.findByFolderAndStatusNot(
                folder,
                FileStatus.DELETED
        );

        for(File file:files){
            file.setStatus(FileStatus.DELETED);

        }
        fileRepository.saveAll(files);

      folder.setStatus(FolderStatus.DELETED);
        folderRepository.save(folder);
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


        Folder folder = findOwnedFolder(folderId);


       return mapToFolderResponse(folder);
    }

    public List<FolderResponse> getAllFolders() {
User user = loggedUser.getLoggedUser();

List<Folder> folders = folderRepository.findByOwnerAndStatus(user, FolderStatus.ACTIVE);

return folders.stream().map(this::mapToFolderResponse).toList();
    }


    public FolderResponse renameFolder(Long folderId, RenameFolderRequest request){

        Folder folder = findOwnedFolder(folderId);

        Optional<Folder> exisitingFolder = folderRepository.findByOwnerAndParentFolderAndNameAndStatus(folder.getOwner(),folder.getParentFolder(), request.getFolderName(), FolderStatus.ACTIVE);
        if(exisitingFolder.isPresent() && exisitingFolder.get().getId()!=folder.getId()){
            throw new FolderAlreadyExistsException("Folder Already Exists");
        }
folder.setName(request.getFolderName());
        Folder updatedFolder = folderRepository.save(folder);

        return mapToFolderResponse(updatedFolder);


    }
@Transactional
    public void deleteFolder(Long folderId) {
        Folder folder = findOwnedFolder(folderId);
        deleteFolderRecursively(folder);
    }
}