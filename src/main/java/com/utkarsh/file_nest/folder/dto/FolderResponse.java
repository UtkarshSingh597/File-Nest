package com.utkarsh.file_nest.folder.dto;

import java.time.LocalDate;

public class FolderResponse {

    private Long folderId;
    private String folderName;
    private Long parentFolderId;

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    private LocalDate createdAt;

    public FolderResponse(LocalDate createdAt, Long folderId, String folderName, Long parentFolderId) {
        this.createdAt = createdAt;
        this.folderId = folderId;
        this.folderName = folderName;
        this.parentFolderId = parentFolderId;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public Long getParentFolderId() {
        return parentFolderId;
    }

    public void setParentFolderId(long parentFolderId) {
        this.parentFolderId = parentFolderId;
    }
}
