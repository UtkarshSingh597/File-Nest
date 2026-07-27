package com.utkarsh.file_nest.folder.service;

import com.utkarsh.file_nest.Exceptions.ForbiddenException;
import com.utkarsh.file_nest.Exceptions.NoContentException;
import com.utkarsh.file_nest.Exceptions.NotFoundException;
import com.utkarsh.file_nest.auth.service.LoggedUser;
import com.utkarsh.file_nest.entity.Folder;
import com.utkarsh.file_nest.entity.User;
import com.utkarsh.file_nest.enums.FileStatus;
import com.utkarsh.file_nest.enums.FolderStatus;
import com.utkarsh.file_nest.folder.dto.CreateFolderRequest;
import com.utkarsh.file_nest.folder.dto.FolderResponse;
import com.utkarsh.file_nest.folder.dto.RenameFolderRequest;
import com.utkarsh.file_nest.repository.FileRepository;
import com.utkarsh.file_nest.repository.FolderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FolderService {

    private static final Duration DELETED_FOLDER_RETENTION = Duration.ofDays(30);

    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;
    private final LoggedUser loggedUser;

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

public Folder findOwnedFolder(Long folderId){
        Folder folder = folderRepository.findById(folderId).orElseThrow(()-> new NotFoundException("Folder Not Found"));
    if(folder.getStatus()==(FolderStatus.DELETED)){
        throw new NoContentException("This Folder was Deleted");
    }
    if(!folder.getOwner().getId().equals(loggedUser.getLoggedUser().getId())){
        throw new NotFoundException("Not Authorized to Access this Folder");
    }

    return folder;
}


    public FolderResponse createFolder(CreateFolderRequest request){
        User user = loggedUser.getLoggedUser();

        Folder parentFolder = null;



        if(request.getParentFolderId() != null ){
            parentFolder = findOwnedFolder(request.getParentFolderId());
        }



        Folder folder = new Folder();
        folder.setName(request.getFolderName());
        folder.setOwner(user);
        folder.setParentFolder(parentFolder);

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
            throw new ForbiddenException("Folder Already Exists");
        }
folder.setName(request.getFolderName());
        Folder updatedFolder = folderRepository.save(folder);

        return mapToFolderResponse(updatedFolder);


    }
@Transactional
public void deleteFolder(Long folderId) {
    Folder folder = findOwnedFolder(folderId);
    List<Long> folderIds = folderRepository.findActiveSubtreeIds(folder.getId());

    fileRepository.softDeleteByFolderIdIn(folderIds, FileStatus.DELETED, LocalDateTime.now());
    folderRepository.softDeleteByIdIn(folderIds, FolderStatus.DELETED, LocalDateTime.now());
}

    @Scheduled(cron = "0 15 3 * * *")
    public void purgeExpiredDeletedFolders() {
        purgeDeletedFoldersOlderThan(DELETED_FOLDER_RETENTION);
    }

    @Transactional
    public void purgeDeletedFoldersOlderThan(Duration retention) {
        LocalDateTime cutoff = LocalDateTime.now().minus(retention);

        while (folderRepository.deleteExpiredLeafFolders(FolderStatus.DELETED, cutoff) > 0) {

        }
    }
}
