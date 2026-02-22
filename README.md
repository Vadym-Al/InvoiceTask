# Spring Boot Technical Assessment

## Task Description

The original task description is available here:
[Task Description.pdf](https://github.com/Vadym-Al/InvoiceTask/blob/main/extra/Developer%20Interview%20Take-Home%20Task-2.pdf)

---

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Docker & Docker Compose

---

## Implementation details

- The application is fully containerized and runs via Docker Compose. The application and PostgreSQL database are started as separate services within the same network.
- All configuration parameters (database credentials, ports, etc.) are externalized and managed through an environment file (.env) to simplify configuration and avoid hardcoded values.
- The solution follows a layered architecture (Controller → Service → Repository) with clear separation of concerns.
- A REST API was implemented according to the task requirements, providing endpoints for managing invoices, line items, and payments.
- Database schema is initialized automatically on application startup.
- Mock/demo data is preloaded to simplify testing and manual verification of functionality.
- Transactions are managed at the service layer using @Transactional.
- DTOs are used to separate API models from persistence entities.
- Input validation is implemented using Jakarta Bean Validation (@Valid).
- Unit tests are implemented using JUnit 4 and Mockito.
- Integration tests are implemented using @SpringBootTest to verify full application flow.

## Database schema

![Database schema](https://github.com/Vadym-Al/InvoiceTask/blob/main/extra/invoice%20diagram.png)

## Running the Application (Docker Only)

### Option 1: Build and Start Containers

```bash
docker-compose up --build
```

### Option 2: Run with Maven

```bash
mvn clean install
mvn spring-boot:run
```
