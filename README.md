# FileShare System

A Spring Boot web app for securely storing and managing files. All file content is encrypted at rest using AES-256/GCM before being saved to the database.

## Tech Stack

- Java 21 + Spring Boot 3.3
- Spring Data JPA + H2 (embedded)
- Flyway (schema migrations)
- Thymeleaf + Bootstrap 5 (UI)
- AES-256/GCM encryption (built-in Java crypto)

## Features

- Upload files (encrypted before storage)
- Download files (decrypted on the fly)
- Soft delete files
- List all active files
- Search files by name

## Running

```bash
mvn spring-boot:run
```

App starts at `http://localhost:8080/files`.

H2 console available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:filesharedb`).

## Configuration

The encryption key is set in `application.properties`:

```properties
encryption.secret-key=12345678901234567890123456789012
```

Replace with a secure 32-character key before deploying anywhere real.
