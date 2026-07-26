# FileNest

A production-style cloud file storage backend inspired by **Google Drive** and **Dropbox**.

⚠️ **SECURITY AUDIT COMPLETED - CRITICAL ISSUES FOUND**  
**Current Score: 6.2/10 - NOT PRODUCTION READY**

---

## ⚠️ Current Status (July 26, 2026)

### Critical Security Issues Found (5 Remaining)
🔴 **DO NOT DEPLOY TO PRODUCTION WITHOUT FIXES**

1. **Hardcoded Credentials** - Database password & JWT secret in source code
2. **No JWT Exception Handling** - Crashes on malformed tokens
3. **Missing Ownership Checks** - Unauthorized file access
4. **No Security Logging** - Can't detect breaches
5. **JWT Filter Unprotected** - Server crashes on bad tokens

✅ **FIXED (2)**
- ✅ File Size Validation (100MB limit implemented)
- ✅ File Type Validation (MIME whitelist + blocked extensions)

### Test Results ✅
- **16/16 Tests Passing** - All file upload validation works
- **100% Success Rate** - Core functionality + security tests
- ⚠️ **Ownership/rate-limiting tests still needed**

### Project Score by Category
| Category | Score | Status |
|----------|-------|--------|
| Architecture | 7.5/10 | ✅ Good |
| Security | 3.5/10 | 🔴 CRITICAL |
| Code Quality | 6.0/10 | ⚠️ Fair |
| Testing | 5.0/10 | ⚠️ Incomplete |
| Documentation | 5.0/10 | ⚠️ Needs Work |
| **Overall** | **6.2/10** | 🚫 NOT READY |

**Full Audit:** See `PROJECT_CONTEXT.md` and `AUDIT_REPORT.md`

---

# Contents

- [Current Status & Audit Results](#-current-status-july-26-2026)
- [Immediate Actions Required](#-immediate-actions-required)
- [Architecture](#architecture)
- [Repository layout](#repository-layout)
- [Authentication System](#authentication-system)
- [Folder Management](#folder-management)
- [File Upload System](#file-upload-system)
- [Database Design](#database-design)
- [Security Model](#security-model)
- [Current Implementation](#current-implementation)
- [Testing](#testing)
- [Known Issues](#known-issues)
- [Future Architecture](#future-architecture)
- [Setup](#setup)
- [API Endpoints](#api-endpoints)
- [Future Work](#future-work)

---

# 🚨 Immediate Actions Required

## Phase 1: CRITICAL FIXES (This Week)
**Time: ~8 hours | Priority: URGENT**

### ✅ 1. Add File Validation (COMPLETED)
```
✅ File Size Validation - 100MB limit (prevents disk exhaustion)
✅ File Type Validation - MIME whitelist + blocked extensions (prevents malware)
- All 16 tests passing
- 7 new validation test cases added
```

### 2. Secure Credentials
```bash
# Move to environment variables - DO NOT COMMIT PASSWORDS
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=$(openssl rand -hex 32)
```

**In application.properties:**
```properties
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
```

### 3. Fix JWT Exception Handling
- Add try-catch in JwtService methods
- Add exception handling in JwtAuthenticationFilter
- Return 401 on invalid tokens instead of 500

### 4. Add File Ownership Verification
```java
// Implement file download with ownership check
@GetMapping("/{fileId}/download")
public ResponseEntity<?> downloadFile(@PathVariable Long fileId) {
    File file = findOwnedFile(fileId); // Verify ownership
    return ResponseEntity.ok().body(resource);
}

private File findOwnedFile(Long fileId) {
    File file = fileRepository.findById(fileId)
        .orElseThrow(() -> new CustomNotFoundException("File not found"));
    
    if (!file.getOwner().getId().equals(loggedUser.getLoggedUser().getId())) {
        throw new ForbiddenException("Not authorized");
    }
    return file;
}
```

## Phase 2: HIGH-PRIORITY FIXES (Next Week)
- [ ] Add rate limiting to auth endpoints
- [ ] Implement file delete endpoint (soft delete)
- [ ] Add security event logging
- [ ] Fix exception types (ForbiddenException vs NotFoundException)
- [ ] Add input validation (email format, password strength)

## Phase 3: CODE QUALITY (Week 3)
- [ ] Fix typos: extractExtention → extractExtension, rootStroage → rootStorage
- [ ] Rename File package to file (Java convention)
- [ ] Convert JwtService field injection to constructor injection
- [ ] Add database indexes for performance
- [ ] Add CORS configuration

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

⚠️ **CURRENT IMPLEMENTATION HAS CRITICAL GAPS** - See [Known Issues](#known-issues)

Every protected resource SHOULD follow:

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

- Other users' folders ✅ (implemented)
- Other users' files ⚠️ (not fully implemented - no download endpoint)
- Deleted resources ✅ (implemented)

### Current Security Issues
- ❌ No file ownership check on download (endpoint missing)
- ❌ No file size validation (DoS risk)
- ❌ No file type validation (malware risk)
- ❌ No rate limiting (brute force risk)
- ❌ Credentials hardcoded in code (breach risk)
- ❌ No exception handling in JWT (crash risk)

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


---

# Testing

## Test Results ✅
```
Total Tests:     16
Passed:          16 ✅
Failed:          0
Errors:          0
Success Rate:    100%
```

### Test Breakdown
- **FileServiceTest**: 14/14 PASSED (1.083s)
  - ✅ Null/empty file validation
  - ✅ File storage to disk
  - ✅ Metadata extraction
  - ✅ Database integration
  - ✅ Error handling
  - ✅ File size validation (100MB limit)
  - ✅ Executable file rejection (.exe, .sh, .bat, .dll)
  - ✅ Valid file acceptance (images, documents, archives)

- **FileUploadSmokeTest**: 1/1 PASSED (0.009s)
  - ✅ Actual file write verification

- **FileNestApplicationTests**: 1/1 PASSED (4.562s)
  - ✅ Spring context loads

### Missing Test Coverage ⚠️
- ❌ File ownership verification tests
- ❌ API endpoint tests
- ❌ Rate limiting tests
- ❌ JWT exception handling tests
- ❌ Credential security tests

**→ See TEST_REPORT.md for detailed results**

### Run Tests
```bash
mvn clean test
```

---

# Known Issues

## 🔴 CRITICAL (Fix before production)
| Issue | Severity | Status |
|-------|----------|--------|
| Hardcoded credentials | CRITICAL | ⚠️ NOT FIXED |
| JWT exception handling | CRITICAL | ⚠️ NOT FIXED |
| ✅ ~~No file size validation~~ | CRITICAL | ✅ FIXED |
| ✅ ~~No file type validation~~ | CRITICAL | ✅ FIXED |
| Missing ownership verification | CRITICAL | ⚠️ NOT FIXED |

## 🟠 HIGH (Fix soon)
| Issue | Severity | Status |
|-------|----------|--------|
| No rate limiting | HIGH | ⚠️ NOT FIXED |
| No security logging | HIGH | ⚠️ NOT FIXED |
| No CORS configuration | HIGH | ⚠️ NOT FIXED |
| Wrong exception types | HIGH | ⚠️ NOT FIXED |

## 🟡 MEDIUM (Fix when possible)
| Issue | Severity | Status |
|-------|----------|--------|
| Code typos | MEDIUM | ⚠️ NOT FIXED |
| Package naming | MEDIUM | ⚠️ NOT FIXED |
| Field injection | MEDIUM | ⚠️ NOT FIXED |
| No DB indexes | MEDIUM | ⚠️ NOT FIXED |

**Full audit:** See `AUDIT_REPORT.md`

---

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