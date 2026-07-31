# FileNest

FileNest is a Spring Boot backend for authenticated folder and file management.
It stores file metadata in MySQL and uploaded bytes on the local filesystem.

## Status

Implemented features include JWT-based registration and login, hierarchical
folders, local file upload, file listing, and soft deletion with scheduled
30-day cleanup.

The current working tree also contains an in-progress folder-download endpoint.
Its controller route and DTO exist, but `FolderService.downloadFolder(...)` has
not been implemented, so it is not yet usable. The project does not currently
compile while that route remains incomplete. See [Known limitations](#known-limitations).

## Stack

- Java 21 and Spring Boot 3.5.4
- Spring MVC, Spring Security, Validation, and Spring Data JPA
- MySQL and Hibernate
- Flyway dependencies (no migrations are currently present)
- JJWT for bearer-token authentication
- Local `uploads/` directory for file content

## Architecture

```text
HTTP request
  -> Controller
  -> Service (authentication, ownership, business rules)
  -> Repository -> MySQL
  -> Local uploads directory (file bytes only)
```

The code is organized under `auth`, `security`, `folder`, `File`, `entity`,
`repository`, and `Exceptions` packages.

## Security model

`POST /api/auth/**` endpoints are public. Every other route requires:

```http
Authorization: Bearer <token>
```

Registration hashes passwords with BCrypt. The JWT subject is the user's email.
Folder and file service methods check that the requested resource is owned by
the authenticated user before acting on it.

Configure database and JWT values using environment variables—never commit
real credentials or a signing key:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/file_nest"
$env:DB_USERNAME = "your-database-user"
$env:DB_PASSWORD = "your-database-password"
$env:JWT_SECRET = "a-long-random-secret-at-least-32-bytes"
# Optional; defaults to 86400000 milliseconds
$env:JWT_EXPIRATION = "86400000"
```

The committed [application.properties](src/main/resources/application.properties)
imports a local [`.env`](.env) file as properties when it exists. Environment
variables remain supported and take precedence. [`.env.example`](.env.example)
is a safe placeholder reference.

## API

### Authentication

| Method | Route | Body | Result |
| --- | --- | --- | --- |
| `POST` | `/api/auth/register` | `name`, `email`, `password` | JWT token |
| `POST` | `/api/auth/login` | `email`, `password` | JWT token |

Registration passwords must contain at least eight characters, including upper-
and lowercase letters, a number, and a special character.

```json
POST /api/auth/register
{
  "name": "Ada Lovelace",
  "email": "ada@example.com",
  "password": "AsecurePassword1!"
}
```

Successful authentication returns:

```json
{ "token": "<jwt>" }
```

### Folders

| Method | Route | Body / parameters | Result |
| --- | --- | --- | --- |
| `POST` | `/api/folders` | `folderName`, optional `parentFolderId` | Creates a folder (`201`) |
| `GET` | `/api/folders` | — | Lists active folders owned by the caller |
| `GET` | `/api/folders/{folderId}` | — | Gets an owned active folder |
| `PATCH` | `/api/folders/{folderId}` | `folderName` | Renames an owned active folder |
| `DELETE` | `/api/folders/{folderId}` | — | Soft-deletes the active folder subtree (`204`) |

Deleting a folder also soft-deletes its files and nested folders. Deleted folder
records are purged after 30 days once they are empty leaf nodes.

`GET /api/folders/{folderId}/download` is declared in the controller but is
**not implemented** and should not be called yet.

### Files

| Method | Route | Body / parameters | Result |
| --- | --- | --- | --- |
| `POST` | `/api/files/upload` | multipart `file`, optional `folderId` | Stores the file and its metadata |
| `GET` | `/api/files?folderId={folderId}` | `folderId` is required | Lists non-deleted files in an owned folder |
| `DELETE` | `/api/files/{fileId}` | — | Soft-deletes an owned file (`204`) |

Uploads are saved under `uploads/` with a UUID filename. Metadata stores the
original name, MIME type, size, owner, optional folder, status, and creation
time. Files over 100 MiB and a set of executable/installer extensions are
rejected. Files soft-deleted for more than 30 days are physically removed and
their records are deleted by the scheduled cleanup task.

There is no individual file-download or restore endpoint yet.

## Setup

1. Install Java 21 and run MySQL.
2. Create the `file_nest` database and a least-privileged application user.
3. Set the required environment variables shown above.
4. Start the application:

   ```bash
   ./mvnw spring-boot:run
   ```

   On Windows, use `mvnw.cmd spring-boot:run`.

The server uses port `8080` by default. Hibernate is currently configured with
`spring.jpa.hibernate.ddl-auto=update`.

## Known limitations

- The JWT filter does not catch malformed, expired, or invalid-token parsing
  exceptions; such requests can result in a server error.
- No rate limiting, CORS configuration, security audit logging, or pagination
  is configured.
- File type validation relies on request-provided MIME type and filename
  extension; it does not inspect file contents.
- Flyway is included but no versioned migration scripts are present; schema
  updates currently rely on Hibernate.
- The current worktree does not compile: `FolderController` refers to the
  unfinished `FolderService.downloadFolder(...)`. Resolve this before running
  the full suite.

## Testing

The application-context startup check passed on July 31, 2026 using the local
`.env` configuration: MySQL connected successfully and Flyway, Hibernate, and
Spring Security initialized. The check used a temporary compile-only stub for
the pending folder-download method; that stub was removed immediately after the
test.

Run the full suite after the folder-download compilation issue is resolved:

```bash
./mvnw test
```

The full test suite remains blocked by the unfinished folder-download method.

## Planned work

- Complete folder archive download and add secure individual file download.
- Add restore support, pagination, database indexes, and API tests.
- Add token-error handling, rate limiting, CORS policy, and audit logging.
- Move file bytes to object storage and add Docker/CI deployment support.
