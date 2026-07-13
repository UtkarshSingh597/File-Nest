package com.utkarsh.file_nest.folder.controller;


import com.utkarsh.file_nest.folder.dto.CreateFolderRequest;
import com.utkarsh.file_nest.folder.dto.FolderResponse;
import com.utkarsh.file_nest.folder.service.FolderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/folders")
@RestController
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService){
        this.folderService = folderService;
    }
    @PostMapping
    public ResponseEntity<FolderResponse>createFolder(@Valid @RequestBody CreateFolderRequest request){
        FolderResponse reponse = folderService.createFolder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }

}
