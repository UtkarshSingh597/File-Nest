package com.utkarsh.file_nest.File.Controller;

import com.utkarsh.file_nest.File.DTO.FileResponse;
import com.utkarsh.file_nest.File.Service.FileService;
import jakarta.validation.constraints.Positive;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api/files")
@RestController
@Validated
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService){
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileResponse>uploadFile(@RequestParam MultipartFile file,
                                                  @RequestParam (required = false)Long folderId){
        FileResponse response = fileService.uploadFile(file,folderId);
        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable @Positive Long fileId) {
        fileService.deleteFile(fileId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public ResponseEntity<List<FileResponse>> getFiles(@RequestParam Long folderId){
        List<FileResponse> response = fileService.getFiles(folderId);
        return ResponseEntity.ok(response);
    }



}
