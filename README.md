# Task Manager API

A full-stack REST API for managing users and tasks, built with Spring Boot and PostgreSQL. Built as a learning project covering layered architecture, JWT authentication, validation, testing, and Docker containerization.

## Tech Stack

- **Java 21**
- **Spring Boot 4** (Web, Data JPA, Security, Validation)
- **PostgreSQL**
- **JWT** (via `jjwt`) for stateless authentication
- **Lombok** for boilerplate reduction
- **JUnit 5 + Mockito** for unit testing
- **Docker & Docker Compose** for containerization
- **Maven** for build/dependency management

## Features

- Full CRUD for `User` and `Task` entities
- `Task` → `User` relationship (many-to-one)
- DTOs for all request/response bodies (entities are never exposed directly)
- Centralized exception handling with correct HTTP status codes (`404`, `400`, `409`, `401`)
- Bean Validation on all incoming requests
- JWT-based registration/login, with a custom filter securing protected endpoints
- Password hashing via BCrypt
- Unit tests for service-layer business logic

## Prerequisites

- **Java 21** or later ([Adoptium Temurin](https://adoptium.net/) recommended)
- **Docker Desktop** (recommended — see [Running with Docker](#running-with-docker) below), **or**
- **PostgreSQL 17** installed locally, if not using Docker

## Running with Docker (recommended)

This is the easiest way to run the project — it starts both the app and a PostgreSQL database together, with no manual database setup required.

1. Clone the repository:
   ```
   git clone <your-repo-url>
   cd taskmanager
   ```
2. Start everything:
   ```
   docker compose up --build
   ```
3. The API will be available at `http://localhost:8080`.

To stop the containers:
```
docker compose down
```
To stop the containers **and** wipe the database (useful if you hit a stale-volume issue during development):
```
docker compose down -v
```

## Running locally without Docker

1. Install PostgreSQL 17 and create a database:
   ```sql
   CREATE DATABASE taskmanager;
   ```
2. Update `src/main/resources/application.properties` with your local Postgres username/password if they differ from the defaults.
3. Run the app:
   ```
   ./mvnw spring-boot:run
   ```
   or run `TaskmanagerApplication.java` directly from your IDE.
4. The API will be available at `http://localhost:8080`.

## Configuration

The app reads its JWT signing secret from an environment variable, falling back to a development-only default if unset:

```properties
jwt.secret=${JWT_SECRET:dev-only-fallback-secret-not-for-production}
```

For any real deployment, set `JWT_SECRET` to a long, random, securely-stored value — never rely on the fallback outside local development.

## API Overview

All endpoints are prefixed at `http://localhost:8080`.

### Auth (public)

| Method | Endpoint         | Description                        |
|--------|------------------|-------------------------------------|
| POST   | `/auth/register` | Create a new user account          |
| POST   | `/auth/login`    | Log in, returns a JWT              |

### Users (requires `Authorization: Bearer <token>`)

| Method | Endpoint       | Description         |
|--------|----------------|----------------------|
| GET    | `/users`       | List all users       |
| GET    | `/users/{id}`  | Get a user by ID     |
| POST   | `/users`       | Create a user         |
| PUT    | `/users/{id}`  | Update a user         |
| DELETE | `/users/{id}`  | Delete a user         |

### Tasks (requires `Authorization: Bearer <token>`)

| Method | Endpoint       | Description               |
|--------|----------------|-----------------------------|
| GET    | `/tasks`       | List all tasks              |
| GET    | `/tasks/{id}`  | Get a task by ID            |
| POST   | `/tasks`       | Create a task (assign to a user) |
| PUT    | `/tasks/{id}`  | Update a task (title, status, reassign) |
| DELETE | `/tasks/{id}`  | Delete a task                |

### Example: Register and log in

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "alice", "email": "alice@example.com", "password": "password123"}'

curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "alice", "password": "password123"}'
```

The login response returns a token:
```json
{ "token": "eyJhbGciOi..." }
```

Use it on subsequent requests:
```bash
curl http://localhost:8080/tasks \
  -H "Authorization: Bearer eyJhbGciOi..."
```

## Running Tests

```
./mvnw test
```

## Project Structure

```
src/main/java/com/hugo/taskmanager/
├── config/        # Security configuration, beans
├── controller/     # REST endpoints
├── dto/            # Request/response shapes
├── entity/          # JPA entities
├── exception/       # Custom exceptions + global handler
├── repository/       # Spring Data JPA repositories
├── security/          # JWT utility + auth filter
└── service/            # Business logic
```
