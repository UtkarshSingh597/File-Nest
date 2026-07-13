package com.utkarsh.file_nest.entity;

import java.time.LocalDate;

import com.utkarsh.file_nest.enums.FileStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


// id
// originalName
// storedName
// size
// mimeType
// owner
// folder
// status
// createdAt

@Entity
@Table(name = "files")
public class File {

public File(){}

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(nullable = false)
private String originalName;

@Column(nullable = false , unique = true)
private String storedName;

@Column(nullable = false)
private Long size;

@Column(nullable = false)
private String mimeType;

@ManyToOne
@JoinColumn(name = "owner_id")
private User owner;

@ManyToOne
@JoinColumn(name = "folder_id")
private Folder folder;

@Enumerated(EnumType.STRING)
private FileStatus status; 

@Column(nullable = false)
private LocalDate createdAt = LocalDate.now();

    public File(Folder folder, String mimeType, String originalName, User owner, Long size, FileStatus status, String storedName) {
       
        this.folder = folder;
       
        this.mimeType = mimeType;
        this.originalName = originalName;
        this.owner = owner;
        this.size = size;
        this.status = status;
        this.storedName = storedName;
    }
public Long getId() {
    return id;
}
public void setId(Long id) {
    this.id = id;
}
public String getOriginalName() {
    return originalName;
}
public void setOriginalName(String originalName) {
    this.originalName = originalName;
}
public String getStoredName() {
    return storedName;
}
public void setStoredName(String storedName) {
    this.storedName = storedName;
}
public Long getSize() {
    return size;
}
public void setSize(Long size) {
    this.size = size;
}
public String getMimeType() {
    return mimeType;
}
public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
}
public User getOwner() {
    return owner;
}
public void setOwner(User owner) {
    this.owner = owner;
}
public Folder getFolder() {
    return folder;
}
public void setFolder(Folder folder) {
    this.folder = folder;
}
public FileStatus getStatus() {
    return status;
}
public void setStatus(FileStatus status) {
    this.status = status;
}
public java.time.LocalDate getCreatedAt() {
    return createdAt;
}
public void setCreatedAt(java.time.LocalDate createdAt) {
    this.createdAt = createdAt;
}


}
