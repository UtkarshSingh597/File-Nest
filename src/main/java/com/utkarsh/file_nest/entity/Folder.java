package com.utkarsh.file_nest.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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