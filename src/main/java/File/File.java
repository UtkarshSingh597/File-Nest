package File;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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

@OneToMany
@Column(nullable = false)
private String owner;

@OneToMany
@JoinColumn(name = "folder_id")
private Folder folder;

@Column(nullable = false)
private String status;

@Column(nullable = false)
private LocalDate createdAt = LocalDate.now();

    public File(LocalDate createdAt, Folder folder, Long id, String mimeType, String originalName, String owner, Long size, String status, String storedName) {
        this.createdAt = createdAt;
        this.folder = folder;
        this.id = id;
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
public String getOwner() {
    return owner;
}
public void setOwner(String owner) {
    this.owner = owner;
}
public String getFolder() {
    return folder;
}
public void setFolder(String folder) {
    this.folder = folder;
}
public String getStatus() {
    return status;
}
public void setStatus(String status) {
    this.status = status;
}
public java.time.LocalDate getCreatedAt() {
    return createdAt;
}
public void setCreatedAt(java.time.LocalDate createdAt) {
    this.createdAt = createdAt;
}


}
