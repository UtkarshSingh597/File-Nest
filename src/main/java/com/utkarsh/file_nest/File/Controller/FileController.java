package com.utkarsh.file_nest.File.Controller;

import com.utkarsh.file_nest.File.DTO.FileResponse;
import com.utkarsh.file_nest.File.Service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/files")
@RestController
public class FileController {

    private FileService fileService;

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
        public ResponseEntity<FileResponse> deleteFile(@PathVariable Long fileId){
        fileService.deleteFile(fileId);
        return ResponseEntity.noContent().build();
        }

}
