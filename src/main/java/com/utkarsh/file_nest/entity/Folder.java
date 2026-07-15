package com.utkarsh.file_nest.entity;

import com.utkarsh.file_nest.enums.FolderStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

// id
// name
// parentFolder
// owner
//files
// createdAt

@Entity
@Table(name = "folders")
public class Folder {
    
    public Folder(){

    }

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_folder_id")
    private Folder parentFolder;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    private LocalDate createdAt = LocalDate.now();

    public FolderStatus getStatus() {
        return status;
    }

    public void setStatus(FolderStatus status) {
        this.status = status;
    }

    @Enumerated(EnumType.STRING)
    private FolderStatus status = FolderStatus.ACTIVE;

    @OneToMany(mappedBy = "folder")
    private List<File> files;


    @OneToMany(mappedBy = "parentFolder")
private List<Folder> subFolders;

    public List<Folder> getSubFolders() {
        return subFolders;
    }
    public void setSubFolders(List<Folder> subFolders) {
        this.subFolders = subFolders;
    }
    public Folder(String name, User owner, Folder parentFolder, List<File> files, List<Folder> subFolders) {
        this.name = name;
        this.owner = owner;
        this.parentFolder = parentFolder;
        this.files = files;
        this.subFolders = subFolders;


    }   public List<File> getFiles() {
        return files;
    }
    public void setFiles(List<File> files) {
        this.files = files;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Folder getParentFolder() {
        return parentFolder;
    }
    public void setParentFolder(Folder parentFolder) {
        this.parentFolder = parentFolder;
    }
    public User getOwner() {
        return owner;
    }
    public void setOwner(User owner) {
        this.owner = owner;
    }
    public LocalDate getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}