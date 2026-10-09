# Task Manager API

A Spring Boot REST API for managing users and tasks with PostgreSQL and JWT authentication.

## Requirements

- Java 21+
- Docker Desktop, or PostgreSQL 17+

## Quick start

### Docker

```bash
docker compose up --build
```

The API runs at `http://localhost:8080`. Stop it with:

```bash
docker compose down
```

### Local PostgreSQL

Create a database named `taskmanager`, update the credentials in
`src/main/resources/application.properties`, then run:

```bash
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

Set `JWT_SECRET` to a long, random value outside local development.

## API

Public endpoints:

| Method | Endpoint | Purpose |
| --- | --- | --- |
| POST | `/auth/register` | Register a user |
| POST | `/auth/login` | Log in and receive a JWT |

All `/users` and `/tasks` endpoints require:

```text
Authorization: Bearer <token>
```

User endpoints:

| Method | Endpoint |
| --- | --- |
| GET | `/users` |
| GET | `/users/{id}` |
| POST | `/users` |
| PUT | `/users/{id}` |
| DELETE | `/users/{id}` |

Task endpoints:

| Method | Endpoint |
| --- | --- |
| GET | `/tasks` |
| GET | `/tasks/{id}` |
| POST | `/tasks` |
| PUT | `/tasks/{id}` |
| DELETE | `/tasks/{id}` |

Example:

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@example.com","password":"password123"}'

curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"password123"}'
```

## Tests

```bash
./mvnw test
```

The project includes unit and controller tests for authentication, users, and tasks.

## Tech stack

Java 21 · Spring Boot 4 · Spring Data JPA · Spring Security · PostgreSQL · JWT · Maven
