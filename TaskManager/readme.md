# Task Manager Application Backend

## Description

This is a task management backend application where users can manage projects and tasks.
The system supports user management, project assignments, and task lifecycle handling.

Main features:
- User management
- Project management
- Task creation and assignment
- Task status workflow
- Pagination, sorting
- Validation and structured error handling
- Basic authentication security

---

## Domain Model

### User Entity

A `User` entity has the following attributes:

- `id` – identity
- `name` – required, not null/blank
- `email` – required, unique, not null/blank

Relations:
- A user can belong to multiple projects
- A user can have multiple assigned tasks

---

### Project Entity

A `Project` entity has the following attributes:

- `id`
- `name` – required
- `description` – required

Relations:
- A project can have multiple users
- A project contains tasks

---

### Task Entity

A `Task` entity has the following attributes:

- `id`
- `title` – required
- `description` – required
- `dueDate` – must be in the future
- `status` – enum-based workflow

Relations:
- A task belongs to a project
- A task can be assigned to a user

---

## Task Status Workflow

Tasks follow a strict lifecycle:

| Status | Description |
|--------|-------------|
| `TODO` | Task created, not yet started |
| `IN_PROGRESS` | Task is actively being worked on |
| `DONE` | Task has been completed |

---

## API Endpoints

### User Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | Get users (paginated) |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users/email/{email}` | Get user by email |
| POST | `/api/users` | Create user |
| DELETE | `/api/users/{id}` | Delete user |

### Project Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/projects` | Get all projects |
| GET | `/api/projects/{id}` | Get project by ID |
| POST | `/api/projects` | Create project |
| DELETE | `/api/projects/{id}` | Delete project |
| POST | `/api/projects/{projectId}/users/{userId}` | Assign user to project |

### Task Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tasks` | Get tasks (paginated) |
| GET | `/api/tasks/{id}` | Get task by ID |
| POST | `/api/tasks` | Create task |
| PUT | `/api/tasks/{id}` | Update task |
| PATCH | `/api/tasks/{id}/status` | Change task status |
| PATCH | `/api/tasks/{id}/assignee` | Assign task |
| DELETE | `/api/tasks/{id}` | Delete task |

---

## Pagination & Sorting

Endpoints returning collections support the following query parameters:

- `page` – default: `0`
- `size` – default: `10`
- `sortBy` – default: `id`

Example:
GET /api/users?page=0&size=5&sortBy=name
---

## Validation

Validation is implemented on two levels:

### 1. Bean Validation (Controller layer)
- `@Valid` annotation
- Field-level constraints

### 2. Business Validation (Service layer)
- Custom `ValidationErrorException`
- Ensures rules even outside controller usage

---

## Error Handling

Centralized via `GlobalExceptionHandler`.

### Error Response Format

```json
{
  "message": "Error message",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "timestamp": "2026-01-01T12:00:00"
}
```

### Supported Error Types

| Code | Description |
|------|-------------|
| 400 | Bad request / validation error |
| 401 | Unauthorized (not authenticated) |
| 403 | Forbidden (no permission) |
| 404 | Resource not found |
| 409 | Conflict |
| 500 | Internal server error |

---

## Security

Basic Authentication is implemented. Users are stored in-memory:

| Username | Password | Role |
|----------|----------|------|
| `user` | `password` | `ROLE_USER` |
| `admin` | `admin` | `ROLE_ADMIN` |

- All endpoints are secured and require authentication
- In Swagger UI, click the **lock icon** (🔒) in the top right to enter credentials

---

## Logging

Logging is implemented on multiple layers:

- **Controller level** – request/response flow
- **Service level** – business operations
- **Exception handling** – errors
- **Optional** request logging filter

Log levels used:

| Level | Purpose |
|-------|---------|
| `INFO` | Business flow |
| `DEBUG` | Detailed internal data |
| `WARN` | Validation / expected issues |
| `ERROR` | Unexpected failures |

---

## Architecture

### Layered Architecture

1. **Controller** – REST endpoints
2. **Service** – business logic
3. **Repository** – data access

### Technologies

- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- MariaDB
- ModelMapper
- Bean Validation
- OpenAPI / Swagger
- JUnit + Integration Tests
- Docker

---

## Testing

- Unit tests for services
- Integration tests for controllers
- Security tests for authentication scenarios
- High test coverage (~100%)

---

## Swagger & OpenAPI

| Resource | URL |
|----------|-----|
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI spec | `http://localhost:8080/v3/api-docs` |

---

## Build & Run

### Build

```bash
./mvnw clean package
```

### Run Locally

```bash
java -jar target/taskmanager.jar
```

### Docker

```bash
# Build image
docker build -t taskmanager .

# Run container
docker run -p 8080:8080 taskmanager
```

---

## Future Improvements

- Role-based authorization (RBAC)
- Advanced filtering (Specification / Criteria API)
- Refresh token / JWT authentication
- Audit logging
- Performance optimization (caching)

---

## Author

Project developed as a backend portfolio project focusing on clean architecture, validation, testing, and production-ready practices.