package com.utkarsh.file_nest.File.DTO;

import java.time.LocalDateTime;

public class FileResponse {
    private Long id;

    private String originalName;

    private Long size;

    private Long folderId;

    private String mimeType;

    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public String getOriginalName() {
        return originalName;
    }

    public Long getSize() {
        return size;
    }

    public Long getFolderId() {
        return folderId;
    }

    public String getMimeType() {
        return mimeType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public FileResponse(Long id, String originalName, Long size, Long folderId, String mimeType, LocalDateTime createdAt) {
        this.id = id;
        this.originalName = originalName;
        this.size = size;
        this.folderId = folderId;
        this.mimeType = mimeType;
        this.createdAt = createdAt;

    }

    public FileResponse() {
    }
}
