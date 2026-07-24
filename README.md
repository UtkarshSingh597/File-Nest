# FileNest

FileNest is a production-inspired cloud file storage backend built with Spring Boot. The project is designed to explore how modern storage platforms such as Google Drive and Dropbox are architected while following clean backend design principles.

The current implementation focuses on authentication, folder management, secure file uploads, and a layered architecture that can later evolve into a distributed storage platform.

---

# Table of Contents

- Overview
- Technology Stack
- Architecture
- Project Structure
- Authentication
- Folder Management
- File Management
- Database Design
- API Endpoints
- Running the Project
- Future Enhancements

---

# Overview

FileNest provides a secure backend for managing folders and files.

Current capabilities include:

- JWT based authentication
- Secure password hashing with BCrypt
- Folder creation and nested folder hierarchy
- Recursive folder deletion using soft delete
- File upload with metadata storage
- Ownership-based authorization
- Local file storage

The application follows a layered architecture to keep responsibilities separated and the codebase maintainable.

---

# Technology Stack

## Backend

- Java 21
- Spring Boot
- Spring MVC
- Spring Security
- Spring Data JPA
- Hibernate

## Database

- MySQL
- Flyway

## Authentication

- JWT
- BCrypt Password Encoder

## Storage

- Local File System

---

# Architecture

```
                    Client
                       │
                       ▼
                REST Controller
                       │
                       ▼
                    Service
               ┌────────┴────────┐
               ▼                 ▼
         Repository        Local Storage
               │
               ▼
             MySQL
```

The controller receives HTTP requests and delegates work to the service layer.

The service contains the application's business logic, performs validation and authorization, interacts with repositories, stores uploaded files, and converts entities into response DTOs.

Repositories communicate with the database using Spring Data JPA.

---

# Project Structure

```
src
└── main
    ├── java
    │   └── com.utkarsh.file_nest
    │       ├── auth
    │       ├── config
    │       ├── entity
    │       ├── Exceptions
    │       ├── File
    │       ├── folder
    │       ├── repository
    │       └── security
    │
    └── resources
        ├── db
        └── application.properties
```

---

# Authentication

Authentication is implemented using JSON Web Tokens (JWT).

## Registration Flow

```
Client

    │

Register Request

    │

Validate Request

    │

Check Existing Email

    │

Encrypt Password

    │

Save User

    │

Generate JWT

    │

Return Token
```

## Login Flow

```
Client

    │

Login Request

    │

Find User

    │

Verify Password

    │

Generate JWT

    │

Return Token
```

All protected endpoints require a valid JWT.

---

# Folder Management

Folders support hierarchical organization.

Example:

```
Documents
│
├── College
│
├── Projects
│   ├── FileNest
│   └── Search Engine
│
└── Notes
```

Each folder belongs to a single user.

Folder operations validate ownership before allowing access.

Folders are soft deleted rather than permanently removed, allowing the application to preserve relationships between resources.

---

# File Management

Uploaded files follow the workflow below.

```
Receive Multipart File

        │

Validate Request

        │

Get Logged User

        │

Verify Folder Access

        │

Generate UUID Filename

        │

Store File Locally

        │

Save Metadata

        │

Return File Response
```

The physical file is stored inside the local uploads directory while metadata is stored inside MySQL.

Metadata includes:

- Original filename
- Generated storage filename
- File size
- MIME type
- Owner
- Parent folder
- Upload timestamp
- File status

Using UUID filenames prevents collisions when different users upload files with identical names.

---

# Database Design

## User

```
id
username
email
password
createdAt
```

## Folder

```
id
name
owner
parentFolder
status
createdAt
```

## File

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

Relationships:

```
User
 │
 ├──── Folder
 │          │
 │          ├──── Folder
 │          │
 │          └──── File
 │
 └──── File
```

---

# API Endpoints

## Authentication

```
POST /api/auth/register

POST /api/auth/login
```

## Folder

```
POST   /api/folders

GET    /api/folders/{id}

PUT    /api/folders/{id}

DELETE /api/folders/{id}
```

## Files

```
POST /api/files/upload
```

Request Parameters

```
MultipartFile file

Long folderId (optional)
```

---

# Running the Project

Clone the repository.

```bash
git clone <repository-url>
```

Configure the database inside `application.properties`.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/file_nest
spring.datasource.username=root
spring.datasource.password=your_password
```

Run Flyway migrations and start the application.

```bash
mvn spring-boot:run
```

The application starts on:

```
http://localhost:8080
```

---

# Design Principles

The project follows several backend engineering principles:

- Layered Architecture
- Separation of Concerns
- Constructor Injection
- DTO-based communication
- Ownership-based Authorization
- Soft Delete Strategy
- RESTful API Design

These principles make the codebase easier to maintain, extend, and test.

---

# Future Enhancements

The current implementation uses local storage as the persistence layer for uploaded files.

Future versions of FileNest will introduce:

- File download
- File deletion
- File sharing
- Search functionality
- MinIO or AWS S3 integration
- Redis caching
- RabbitMQ for asynchronous processing
- Virus scanning
- Thumbnail generation
- Docker support
- Unit and integration testing
- CI/CD pipeline

---

# Author

**Utkarsh Singh**

FileNest is a learning-focused backend engineering project that aims to demonstrate production-oriented software design, scalable architecture, and modern Java backend development using Spring Boot.
