# FileNest - Project Context

A production-style cloud file storage platform inspired by Google Drive and Dropbox.

The primary goal of this project is to learn backend engineering by implementing production-quality architecture, security, scalability, and clean code practices.

---

# Project Goal

Build a scalable cloud storage backend while learning:

- Spring Boot ecosystem
- Authentication & Authorization
- Database design
- REST API development
- Distributed systems concepts
- Cloud storage integration
- Production-ready architecture

---

# Tech Stack

## Backend

- Java 21
- Spring Boot 3.5
- Spring Security
- Spring Data JPA
- Hibernate

## Database

- MySQL
- Flyway

## Authentication

- JWT
- BCrypt Password Encoding

## Planned Technologies

- Redis
- RabbitMQ
- MinIO
- AWS S3
- Docker
- Docker Compose
- JUnit
- Mockito
- GitHub Actions

---

# Architecture

```
Client
   │
   ▼
Controller
   │
   ▼
Service
   │
   ▼
Repository
   │
   ▼
MySQL
```

## Responsibilities

### Controller

- Accept HTTP requests
- Validate DTOs
- Return responses
- No business logic

### Service

- Business logic
- Validation
- Authorization
- Transaction management

### Repository

- Database operations only
- No business logic

---

# Package Structure

```
com.utkarsh.file_nest
│
├── auth
│   ├── controller
│   ├── dto
│   └── service
│
├── config
│
├── entity
│
├── repository
│
├── exception
│
├── security
│
├── user
│
├── folder
│
└── file
```

---

# Coding Standards

- Constructor Injection
- No Field Injection (`@Autowired`)
- DTOs for Requests & Responses
- ResponseEntity from Controllers
- Custom Exceptions
- Global Exception Handling (`@RestControllerAdvice`)
- Optional instead of null
- BCrypt Password Hashing
- Never expose entities directly
- Thin Controllers
- Business Logic inside Services
- Constructor-based dependency injection
- Soft Delete instead of Hard Delete where applicable
- Reusable helper methods to avoid duplicated business logic

---

# Current Progress

## Database

- [x] User Entity
- [x] Folder Entity
- [x] File Entity
- [x] FolderStatus Enum
- [x] FileStatus Enum
- [x] JPA Repositories

---

## Authentication

- [x] Register
- [x] Login
- [x] JWT Service
- [x] JWT Authentication Filter
- [x] Spring Security Configuration
- [x] BCrypt Password Encoding
- [x] Protected APIs
- [x] Bearer Token Authentication
- [x] LoggedUser Service

---

## Folder Management

### Implemented

- [x] Create Folder
- [x] Get Folder
- [x] Get All Folders
- [x] Rename Folder
- [x] Recursive Soft Delete
- [x] Nested Folder Support
- [x] Folder Ownership Validation
- [x] Duplicate Folder Name Validation
- [x] Folder DTO Mapping
- [x] Folder Status Filtering

### Folder Design

Folders support recursive hierarchy.

Folder deletion is implemented using **recursive soft delete**.

```
Parent Folder
      │
      ├── Child Folder
      │       └── Child Folder
      │
      └── Files
```

Deleting a folder recursively marks:

- Parent Folder
- Child Folders
- Files

as `DELETED`.

---

## Exception Handling

- [x] EmailAlreadyExistsException
- [x] InvalidCredentialsException
- [x] FolderNotFoundException
- [x] FolderAlreadyExistsException
- [x] FolderAccessDeniedException
- [x] GlobalExceptionHandler

---

## Testing

Authentication tested using Postman

- [x] Register
- [x] Login
- [x] JWT Authentication

Folder APIs tested

- [x] Create Folder
- [x] Get Folder
- [x] Get All Folders
- [x] Rename Folder
- [x] Recursive Delete

---

# Folder Service Design

```
Controller

↓

FolderService

↓

getOwnedFolder()

↓

Business Logic

↓

Repository
```

Reusable helper methods

- mapToFolderResponse()
- getOwnedFolder()

Authentication is handled through:

- LoggedUser Service

---

# Current Feature

## File Management

Next module:

- Upload File

---

# Roadmap

## Authentication

- [x] Registration
- [x] Login
- [x] JWT Authentication
- [ ] Role-Based Authorization

---

## Folder Management

- [x] Create Folder
- [x] Get Folder
- [x] Get All Folders
- [x] Rename Folder
- [x] Recursive Soft Delete
- [ ] Restore Folder
- [ ] Move Folder

---

## File Management

- [ ] Upload File
- [ ] Download File
- [ ] Get File Metadata
- [ ] List Files
- [ ] Rename File
- [ ] Delete File
- [ ] Restore File

---

## Sharing

- [ ] Public Links
- [ ] Shared Folders
- [ ] Access Permissions

---

## Cloud Storage

- [ ] MinIO
- [ ] AWS S3

---

## Performance

- [ ] Redis
- [ ] RabbitMQ
- [ ] Async Processing

---

## Deployment

- [ ] Docker
- [ ] Docker Compose
- [ ] GitHub Actions

---

## Testing

- [ ] JUnit
- [ ] Mockito
- [ ] Integration Tests

---

# Learning Objectives

- Spring Boot
- Spring Security
- JWT
- REST APIs
- Database Design
- Flyway
- File Storage
- Object Storage
- Distributed Systems
- Redis
- RabbitMQ
- Docker
- CI/CD
- Testing

---

# Development Principles

- Thin Controllers
- Business Logic inside Services
- Repository only accesses the database
- DTOs for API communication
- SOLID Principles
- Clean Architecture
- Soft Delete instead of Hard Delete
- Recursive algorithms for hierarchical data
- Readable and maintainable code
- Production-ready design

---

# Current Status

## Completed Milestone

Authentication ✅

Folder Management ✅

## Current Milestone

File Management

### Next Feature

- File Upload API
- MultipartFile
- Metadata Storage
- Local Storage (before MinIO)
