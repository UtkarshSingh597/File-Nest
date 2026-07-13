#  FileNest - Project Context

A production-style cloud file storage platform inspired by **Google Drive** and **Dropbox**.

The primary goal of this project is to learn **backend engineering** by implementing production-quality architecture, security, scalability, and clean code practices.

---

#  Project Goal

Build a scalable cloud storage backend while learning:

- Spring Boot ecosystem
- Authentication & Authorization
- Database design
- REST API development
- Distributed systems concepts
- Cloud storage integration
- Production-ready architecture

---

#  Tech Stack

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

- JWT (JSON Web Tokens)
- BCrypt Password Encoding

## Planned Technologies

- Redis
- RabbitMQ
- MinIO / AWS S3
- Docker
- Docker Compose
- JUnit
- Mockito
- GitHub Actions

---

# 🏛️ Architecture

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

### Responsibilities

### Controller

- Accept HTTP requests
- Validate DTOs
- Return responses
- No business logic

### Service

- Business logic
- Validation
- Security checks
- Transaction management

### Repository

- Database operations only
- No business logic

---

# 📂 Package Structure

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

#  Coding Standards

- ✅ Constructor Injection
- ❌ No Field Injection (`@Autowired`)
- ✅ DTOs for Requests & Responses
- ✅ ResponseEntity from Controllers
- ✅ Custom Exceptions
- ✅ Global Exception Handling (`@RestControllerAdvice`)
- ✅ Use Optional instead of null
- ✅ BCrypt Password Hashing
- ✅ Never expose Entities directly
- ✅ Thin Controllers
- ✅ Business Logic inside Services
- ✅ Clean, Modular Code

---

#  Current Progress

## Database

- [x] User Entity
- [x] Folder Entity
- [x] File Entity
- [x] JPA Repositories

## Authentication

- [x] Register DTO
- [x] Login DTO
- [x] AuthResponse DTO
- [x] AuthController
- [x] Registration Service
- [x] Login Service
- [x] Password Encoding (BCrypt)
- [x] JWT Service
- [x] JWT Authentication Filter
- [x] Spring Security Configuration
- [x] Protected Endpoints
- [x] Bearer Token Authentication

## Exception Handling

- [x] EmailAlreadyExistsException
- [x] InvalidCredentialsException
- [x] GlobalExceptionHandler

## Testing

- [x] Registration tested using Postman
- [x] Login tested using Postman
- [x] JWT Authentication tested

---

#  Current Feature

## Folder Management

Authentication is complete.

Currently implementing:

- Create Folder API

---

#  Authentication Flow

## Registration

```
RegisterRequest
        │
        ▼
Validate DTO
        │
        ▼
Check Duplicate Email
        │
        ▼
Hash Password (BCrypt)
        │
        ▼
Create User
        │
        ▼
Save User
        │
        ▼
Generate JWT
        │
        ▼
Return AuthResponse
```

---

## Login

```
LoginRequest
        │
        ▼
Validate DTO
        │
        ▼
Find User by Email
        │
        ▼
Verify Password
        │
        ▼
Generate JWT
        │
        ▼
Return AuthResponse
```

---

#  Roadmap

## Authentication

- [x] Registration
- [x] Login
- [x] JWT Authentication
- [ ] Role-Based Authorization

## Folder Management

- [ ] Create Folder
- [ ] Rename Folder
- [ ] Delete Folder
- [ ] Nested Folders

## File Management

- [ ] Upload File
- [ ] Download File
- [ ] Delete File
- [ ] Restore File
- [ ] File Metadata

## Sharing

- [ ] Public Links
- [ ] Shared Folders
- [ ] Access Permissions

## Cloud Storage

- [ ] MinIO Integration
- [ ] AWS S3 Support

## Performance

- [ ] Redis Cache
- [ ] RabbitMQ
- [ ] Async Processing

## Deployment

- [ ] Docker
- [ ] Docker Compose
- [ ] GitHub Actions CI/CD

## Testing

- [ ] Unit Tests (JUnit)
- [ ] Mockito
- [ ] Integration Tests

---

#  Learning Objectives

This project is designed to provide hands-on experience with:

- Spring Boot
- Spring Security
- JWT Authentication
- REST API Design
- Database Design
- Flyway Migrations
- File Storage Systems
- Distributed Systems
- Caching
- Message Queues
- Cloud Storage
- Docker
- CI/CD Pipelines

---

#  Development Principles

- Keep Controllers Thin
- Business Logic Lives in Services
- Repository Layer Only Accesses the Database
- Prefer Composition over Complexity
- Follow SOLID Principles
- Write Readable Code
- Avoid Premature Optimization
- Prioritize Maintainability
- Build Production-Ready Features

---

#  Current Status

**Current Milestone:** Folder Management

**Next Task:** Implement the Create Folder endpoint.