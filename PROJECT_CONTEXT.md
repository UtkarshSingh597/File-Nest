# FileNest - Project Context

## Project Goal

FileNest is a production-style cloud file storage platform inspired by Google Drive and Dropbox.

This project is built primarily for learning backend engineering and should follow production-quality architecture and best practices.

---

# Tech Stack

Backend
- Java 21
- Spring Boot 3.5
- Spring Security
- Spring Data JPA
- Hibernate

Database
- MySQL
- Flyway

Authentication
- JWT
- BCrypt Password Encoding

Future
- Redis
- RabbitMQ
- MinIO (or AWS S3)
- Docker
- JUnit & Mockito
- GitHub Actions

---

# Architecture

Controller
↓

Service

↓

Repository

↓

MySQL

Business logic belongs only inside services.

Controllers should remain thin.

Repositories should only access the database.

---

# Package Structure

com.utkarsh.file_nest

auth
    controller
    dto
    service

config

entity

repository

exception

file

folder

user

security

---

# Coding Style

Use constructor injection.

Never use field injection (@Autowired).

Always use DTOs for requests and responses.

Use ResponseEntity from controllers.

Use custom exceptions.

Handle exceptions globally using @RestControllerAdvice.

Use Optional from repositories instead of null.

Hash passwords using BCrypt.

Never expose entity objects directly from controllers.

---

# Current Progress

Completed

- User Entity
- Folder Entity
- File Entity

- JPA Repositories

- Registration DTO
- Login DTO
- AuthResponse DTO

- AuthController

- AuthService (Registration)

- PasswordEncoder

- EmailAlreadyExistsException

- GlobalExceptionHandler

- Registration tested in Postman

---

# Current Feature

Authentication

Working on Login endpoint.

JWT has NOT been implemented yet.

---

# Authentication Flow

Registration

RegisterRequest

↓

Validate DTO

↓

Check duplicate email

↓

Hash password

↓

Create User

↓

Save User

↓

Return AuthResponse

Login

LoginRequest

↓

Find user by email

↓

Compare password

↓

Generate JWT

↓

Return AuthResponse

---

# Future Features

Authentication

- Login
- JWT Service
- JWT Filter
- Role Based Authorization

Storage

- Upload File
- Download File
- Delete File
- Restore File

Folders

- Nested folders
- Move files
- Rename

Sharing

- Public links
- Shared folders

Cloud

- MinIO
- AWS S3

Performance

- Redis Cache
- RabbitMQ

Deployment

- Docker
- Docker Compose

Testing

- JUnit
- Mockito
- Integration Tests

---

# Coding Principles

Do not generate large amounts of code without explanation.

Prefer clean architecture over shortcuts.

Explain important design decisions.

Follow Spring Boot best practices.

Keep code modular.

Avoid unnecessary complexity.

Favor readability.
