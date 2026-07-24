# FileNest - GitHub Copilot Instructions

## Project Overview

FileNest is a production-style cloud file storage backend inspired by Google Drive and Dropbox.

The goal of this project is to learn and implement professional backend engineering practices:

- REST API development
- Authentication and Authorization
- Database design
- File storage systems
- Distributed systems
- Cloud storage architecture
- Production-ready Spring Boot development

Always prioritize clean architecture, maintainability, security, and scalability.

---

# Technology Stack

## Backend

- Java 21
- Spring Boot 3.5
- Spring MVC
- Spring Security
- Spring Data JPA
- Hibernate

## Database

- MySQL
- Flyway migrations

## Authentication

- JWT Authentication
- BCrypt password hashing

## Planned Technologies

- MinIO
- AWS S3
- Redis
- RabbitMQ
- Docker
- Docker Compose
- JUnit 5
- Mockito
- GitHub Actions

---

# Architecture Rules

Follow this layered architecture:

```
Controller
      |
      v
Service
      |
      v
Repository
      |
      v
Database
```

---

# Controller Layer

Controllers must remain thin.

Controllers should only:

- Receive HTTP requests
- Validate DTOs
- Call services
- Return ResponseEntity


Controllers must NOT contain:

- Business logic
- Database queries
- Authorization logic
- File processing logic


Example:

```java
@PostMapping
public ResponseEntity<ResponseDTO> create(
        @Valid @RequestBody RequestDTO request
){
    return ResponseEntity.ok(service.create(request));
}
```

---

# Service Layer

All business logic belongs inside services.

Services handle:

- Validation
- Authorization checks
- Transactions
- Business rules
- DTO mapping


Rules:

- Use constructor injection only
- Never use field injection
- Never put database queries directly here
- Never expose entities directly


Example:

```java
@Service
public class FileService {

    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository){
        this.fileRepository = fileRepository;
    }
}
```

---

# Repository Layer

Repositories only access the database.

Allowed:

- findById()
- save()
- delete()
- custom finder methods


Do not put:

- validation
- authorization
- business rules

inside repositories.

---

# Entity Rules

Entities represent database tables.

Use:

- JPA annotations
- Clear relationships
- Proper cardinality


Never return entities from controllers.

Wrong:

```java
return folder;
```


Correct:

```java
return new FolderResponse(...);
```

---

# DTO Rules

All API communication must use DTOs.

Use:

Request DTOs:

```
RegisterRequest
LoginRequest
CreateFolderRequest
RenameFolderRequest
```

Response DTOs:

```
AuthResponse
FolderResponse
FileResponse
```

Do not expose:

- Passwords
- Internal file paths
- Database relationships unnecessarily
- Stored filenames

---

# Dependency Injection

Always use constructor injection.

Correct:

```java
public FolderService(
        FolderRepository folderRepository
){
    this.folderRepository = folderRepository;
}
```

Avoid:

```java
@Autowired
private FolderRepository folderRepository;
```

---

# Exception Handling

Use centralized exception handling:

```java
@RestControllerAdvice
```

Exceptions should not be handled inside controllers.

Use proper HTTP statuses:

```
400 BAD_REQUEST
401 UNAUTHORIZED
403 FORBIDDEN
404 NOT_FOUND
409 CONFLICT
```

---

# Security Rules

Authentication:

- JWT based authentication
- BCrypt password encoding


Authorization:

Every protected resource must verify ownership.


Before accessing any resource:

1. Check resource exists
2. Check resource status
3. Check ownership


Never trust IDs received from clients.

---

# Folder Module

Folders support nested hierarchy.

Example:

```
Documents

    |
    └── Java

            |
            └── Spring
```


Folder relationship:

```java
@ManyToOne
private Folder parentFolder;
```


Folder deletion uses soft delete.

Never physically delete folders.

Use:

```
ACTIVE
DELETED
```


Deleting a folder should recursively mark:

- Child folders
- Files

as deleted.

---

# File Module

Files consist of:

## Metadata stored in MySQL

```
id
originalName
storedName
size
mimeType
owner
folder
status
createdAt
```


## Actual file storage

Current:

```
Local Storage
```


Future:

```
MinIO
AWS S3
```


Never store binary files directly in MySQL.

---

# File Upload Flow

Implement uploads in this order:

```
Receive MultipartFile

        ↓

Validate file

        ↓

Get logged-in user

        ↓

Verify folder ownership

        ↓

Generate UUID filename

        ↓

Save file

        ↓

Save metadata

        ↓

Return FileResponse
```


Always validate:

- Empty files
- File size
- File type

Never trust uploaded filenames.

---

# Current Project Progress

## Completed

Authentication:

✅ Registration

✅ Login

✅ JWT generation

✅ JWT validation

✅ Spring Security configuration


Folder Management:

✅ Create Folder

✅ Get Folder

✅ Get All Folders

✅ Rename Folder

✅ Nested folders

✅ Ownership validation

✅ Recursive soft delete


---

# Current Task

File Management Module


Implement:

1. File Upload
2. File Metadata
3. Download File
4. Delete File
5. Restore File

---

# Development Rules

Before modifying code:

1. Understand existing architecture
2. Follow current project patterns
3. Avoid unnecessary refactoring
4. Do not introduce new dependencies without reason


When adding features:

1. Entity
2. Repository
3. DTO
4. Service
5. Controller
6. Exception handling
7. Testing

---

# Git Commit Style

Use conventional commits:

```
feat:
fix:
refactor:
docs:
test:
```

Examples:

```
feat(folder): implement recursive folder deletion

feat(file): add file upload

fix(auth): handle invalid JWT
```

---

# Coding Philosophy

Prioritize:

- Clean code
- Security
- Scalability
- Maintainability
- Understanding


Avoid:

- Overengineering
- Duplicate logic
- Exposing entities
- Mixing responsibilities
- Premature optimization


The goal is to build FileNest as a production-quality backend while learning industry-level Spring Boot engineering practices.