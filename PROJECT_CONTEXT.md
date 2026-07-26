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

# 🚨 CRITICAL STATUS UPDATE (July 26, 2026)

## Security Audit Results
**Overall Score: 6.2/10** - CRITICAL SECURITY GAPS FOUND (5 remaining, 2 fixed)

### 🔴 Critical Issues (5 Remaining)
1. **Hardcoded credentials in source code** - Database password & JWT secret exposed
2. **JWT exception handling missing** - Unhandled exceptions cause 500 errors
3. **Missing file ownership verification** - Unauthorized access possible
4. **Orphaned files on DB failure** - Storage leaks
5. **No exception handling in JWT filter** - Server crashes on bad tokens

### ✅ FIXED (2)
- ✅ **File size validation** - 100MB limit implemented (prevents disk exhaustion DoS)
- ✅ **File type validation** - MIME whitelist + blocked extensions (prevents malware upload)

### 🟠 High-Risk Issues (7)
- No rate limiting (brute force attacks)
- No security event logging
- No CORS configuration
- Wrong exception types (information disclosure)
- No JWT refresh token mechanism

### 🟡 Medium Issues (11)
- Typos in code (extractExtention, rootStroage, unknow-file)
- Inconsistent exception naming
- Field injection violates rules
- Wrong package naming (File instead of file)
- No database indexes
- No input validation

**→ See AUDIT_REPORT.md for full details**

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

## File Management

### Implemented
- [x] File Entity
- [x] File Repository
- [x] File DTO
- [x] File Upload API ✅ (with security validation)
- [x] Local File Storage
- [x] File Metadata Storage
- [x] File Size Validation (100MB limit)
- [x] File Type Validation (MIME whitelist + blocked extensions)
- [ ] File Download API
- [ ] File Delete API
- [ ] File Metadata Retrieval
- [ ] List Files

### Security Status ✅ IMPROVED
- ✅ File size validation (100MB limit implemented)
- ✅ File type validation (MIME whitelist: 35 types, blocked extensions: 19)
- ❌ No ownership verification for download (next priority)
- ❌ No soft delete implementation
- ⚠️ Incomplete feature set

---

## Testing Results

### Unit Tests ✅
- [x] FileServiceTest: 14/14 PASSED ✅ (added 7 validation tests)
- [x] FileUploadSmokeTest: 1/1 PASSED ✅
- [x] FileNestApplicationTests: 1/1 PASSED ✅

**Total: 16/16 tests passing (100% success rate)**

### Test Coverage
✅ Well-tested:
- File validation (null, empty)
- File storage to disk
- Metadata extraction
- Database integration
- File size validation (100MB limit)
- File type validation (blocked extensions, MIME types)
- Valid file acceptance (images, documents, archives)

❌ Not tested (PRIORITY):
- File ownership verification
- API endpoints
- Rate limiting
- JWT exception handling
- Authentication

**→ See TEST_REPORT.md for details**

---

## Code Quality Issues

### Typos Found (must fix)
- Line 28: `extractExtention` → should be `extractExtension`
- Line 34: `rootStroage` → should be `rootStorage`
- Line 55: `unknow-file` → should be `unknown-file`

### Architecture Violations
- JwtService uses field injection (@Value) instead of constructor injection
- File package uses PascalCase instead of lowercase

---

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

- [x] Upload File ✅ (with size & type validation)
- [x] File Size Validation ✅ (100MB limit)
- [x] File Type Validation ✅ (MIME whitelist + blocked extensions)
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