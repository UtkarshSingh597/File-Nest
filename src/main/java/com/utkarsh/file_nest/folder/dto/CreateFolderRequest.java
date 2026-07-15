package com.utkarsh.file_nest.folder.dto;

public class CreateFolderRequest {

    private String folderName;
    private Long parentFolderId;

    public CreateFolderRequest(){

    }

    public Long getParentFolderId() {
        return parentFolderId;
    }

    public void setParentFolderId(Long parentFolder) {
        this.parentFolderId = parentFolderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
