package com.utkarsh.file_nest.folder.dto;

import jakarta.validation.constraints.NotBlank;

public class RenameFolderRequest {
    public @NotBlank String getFolderName() {
        return folderName;
    }


    public RenameFolderRequest() {
    }

    public void setFolderName(@NotBlank String folderName) {
        this.folderName = folderName;
    }

    @NotBlank
private String folderName;
}
