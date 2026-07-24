# FileNest

A production-style cloud file storage backend inspired by **Google Drive** and **Dropbox**.

FileNest is a backend engineering project focused on building a scalable file management system while learning real-world concepts like authentication, authorization, database modeling, REST APIs, file storage architecture, and distributed system design.

The goal is not only to build a working application, but to understand how production storage systems are designed.

---

# Contents

- [What this is](#what-this-is)
- [Architecture](#architecture)
- [Repository layout](#repository-layout)
- [Authentication System](#authentication-system)
- [Folder Management](#folder-management)
- [File Upload System](#file-upload-system)
- [Database Design](#database-design)
- [Security Model](#security-model)
- [Current Implementation](#current-implementation)
- [Future Architecture](#future-architecture)
- [Setup](#setup)
- [API Endpoints](#api-endpoints)
- [Future Work](#future-work)

---

# What this is

FileNest is a cloud storage backend that allows users to:

- Create and manage folders
- Create nested folder structures
- Upload files
- Store file metadata
- Secure resources using JWT authentication
- Control access through ownership validation

The project follows a production-style layered architecture:

- Controller layer for API handling
- Service layer for business logic
- Repository layer for database communication
- DTO-based request and response handling

The current implementation focuses on building a strong backend foundation before introducing advanced distributed system components.

---

# Architecture

```
                    Client
                      |
                      |
                      v

              REST API Request

                      |
                      v

              ┌───────────────┐
              │  Controller   │
              └───────┬───────┘
                      |
                      v

              ┌───────────────┐
              │    Service    │
              └───────┬───────┘
                      |
          ┌───────────┴───────────┐
          v                       v

   ┌─────────────┐        ┌─────────────┐
   │ Repository  │        │ File Storage│
   └──────┬──────┘        └──────┬──────┘
          |                      |
          v                      v

       MySQL                 Local Storage
```

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
- Flyway

## Authentication

- JWT Authentication
- BCrypt Password Hashing

## Current Storage

- Local File Storage

## Planned Infrastructure

- MinIO / AWS S3
- Redis
- RabbitMQ
- Docker
- CI/CD Pipeline

---

# Repository Layout

```
file-nest/

├── src/main/java/com/utkarsh/file_nest

│
├── auth/
│   ├── controller/
│   ├── dto/
│   └── service/
│
├── security/
│
├── entity/
│
├── repository/
│
├── Exceptions/
│
├── folder/
│   ├── controller/
│   ├── dto/
│   └── service/
│
├── File/
│   ├── controller/
│   ├── DTO/
│   └── Service/
│
└── resources/
    └── application.properties
```

---

# Authentication System

FileNest uses JWT-based authentication.

## Registration Flow

```
Register Request

        |
        v

Validate DTO

        |
        v

Check Existing Email

        |
        v

Hash Password using BCrypt

        |
        v

Save User

        |
        v

Generate JWT Token

        |
        v

Return AuthResponse
```

---

## Login Flow

```
Login Request

        |
        v

Find User

        |
        v

Verify Password

        |
        v

Generate JWT

        |
        v

Return Token
```

---

# Security Model

Every protected resource follows:

```
Request

  |
  v

Authenticate User

  |
  v

Find Resource

  |
  v

Check Ownership

  |
  v

Allow / Reject
```

Users cannot access:

- Other users' folders
- Other users' files
- Deleted resources

---

# Folder Management

FileNest supports hierarchical folders.

Example:

```
Documents

    |
    ├── Projects

    |       |
    |       └── FileNest

    |
    └── Photos
```

Implemented using:

```java
@ManyToOne
private Folder parentFolder;
```

---

## Folder Features

Implemented:

✅ Create Folder

✅ Nested Folder Creation

✅ Rename Folder

✅ Folder Ownership Validation

✅ Recursive Folder Delete


---

# Soft Delete System

Folders are not permanently removed.

Instead:

```
ACTIVE

DELETED
```

Deleting a folder:

```
Parent Folder

      |
      |
      +---- Child Folder

      |
      |
      +---- Files
```

recursively marks everything as deleted.

---

# File Management

## File Upload Flow

```
MultipartFile Request

        |
        v

Validate File

        |
        v

Get Logged User

        |
        v

Verify Folder Access

        |
        v

Generate UUID Filename

        |
        v

Store File

        |
        v

Save Metadata

        |
        v

Return FileResponse
```

---

# File Storage Design

Files are separated into two parts:

## Metadata

Stored in MySQL:

```
File

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

---

## Actual File

Stored separately:

```
uploads/

    |
    |
    └── uuid-generated-file.pdf
```

---

# Why UUID Filenames?

Original filename:

```
resume.pdf
```

can create conflicts:

```
User A
resume.pdf


User B
resume.pdf
```

Instead:

```
8f4a2c91-resume.pdf

c12b7d22-resume.pdf
```

Every stored file gets a unique identifier.

---

# Database Design

## User

```
User

id

username

email

password

createdAt
```

---

## Folder

```
Folder

id

name

owner

parentFolder

status

createdAt
```

---

## File

```
File

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

---

# Current Implementation

## Completed

### Authentication

✅ User Registration

✅ User Login

✅ JWT Generation

✅ JWT Validation

✅ Spring Security Configuration

✅ BCrypt Password Encoding


### Folder System

✅ Folder Creation

✅ Nested Folder Support

✅ Ownership Validation

✅ Soft Delete

✅ Recursive Folder Deletion


### File System

✅ File Entity

✅ File Repository

✅ File DTO

✅ File Upload API

✅ Local File Storage

✅ File Metadata Storage


---

# API Endpoints

## Authentication

### Register

```
POST /api/auth/register
```

### Login

```
POST /api/auth/login
```

---

## Folder

### Create Folder

```
POST /api/folders
```

### Rename Folder

```
PUT /api/folders/{id}
```

### Delete Folder

```
DELETE /api/folders/{id}
```

---

## Files

### Upload File

```
POST /api/files/upload
```

Request:

```
MultipartFile file

folderId(optional)
```

---

# Future Architecture

The current system is a modular monolith.

Future evolution:

```
                 API Gateway

                      |

        ----------------------------

        |             |            |

    User Service  File Service  Notification Service


                      |

                 Message Queue

                 (RabbitMQ)
```

---

# Planned Features

## File Features

- Download files
- Delete files
- Restore deleted files
- File versioning


## Cloud Storage

- MinIO integration
- AWS S3 support


## Performance

- Redis caching
- Database indexing
- Pagination


## Distributed Processing

RabbitMQ will handle:

- Virus scanning
- Thumbnail generation
- Background processing


## DevOps

- Docker
- Docker Compose
- GitHub Actions
- Deployment pipeline


## Testing

- JUnit
- Mockito
- Integration Testing

---

# Setup

Clone repository:

```bash
git clone <repository-url>
```

Configure MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/file_nest
spring.datasource.username=<username>
spring.datasource.password=<password>
```

Run application:

```bash
mvn spring-boot:run
```

Application starts on:

```
localhost:8080
```

---

# Development Philosophy

FileNest follows:

- Clean Architecture
- Separation of Responsibilities
- DTO-based communication
- Secure resource access
- Maintainable code structure

The project is being developed incrementally:

```
Working Feature

        ↓

Improve Architecture

        ↓

Add Scalability

        ↓

Introduce Distributed Systems
```

---

# Future Goal

Transform FileNest from a simple cloud storage backend into a production-style distributed storage platform while gaining practical experience with backend engineering and system design.