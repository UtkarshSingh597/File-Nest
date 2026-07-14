package com.utkarsh.file_nest.folder.controller;


import com.utkarsh.file_nest.folder.dto.CreateFolderRequest;
import com.utkarsh.file_nest.folder.dto.FolderResponse;
import com.utkarsh.file_nest.folder.dto.RenameFolderRequest;
import com.utkarsh.file_nest.folder.service.FolderService;
import com.utkarsh.file_nest.repository.FolderRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/folders")
@RestController
public class FolderController {

    private final FolderService folderService;
    private final FolderRepository folderRepository;


    public FolderController(FolderService folderService, FolderRepository folderRepository){
        this.folderService = folderService;
        this.folderRepository = folderRepository;
    }
    @PostMapping
    public ResponseEntity<FolderResponse>createFolder(@Valid @RequestBody CreateFolderRequest request){
        FolderResponse response = folderService.createFolder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{folderId}")
   public ResponseEntity<FolderResponse>getFolder(@PathVariable Long folderId){
        FolderResponse response = folderService.getFolder(folderId);
        return ResponseEntity.ok(response);

    }
    @GetMapping()
    public ResponseEntity<List<FolderResponse>>getAllFolder(){
        List<FolderResponse> response = folderService.getAllFolders();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{folderId}")
    public ResponseEntity<FolderResponse>renameFolder(@Valid  @PathVariable Long folderId, @RequestBody RenameFolderRequest request){
        FolderResponse response = folderService.renameFolder(folderId,request);
        return  ResponseEntity.ok(response);
    }
}
