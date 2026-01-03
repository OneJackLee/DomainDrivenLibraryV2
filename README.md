# Domain Driven Library

Domain Driven Library is a sample library management system with Domain-Driven Design (DDD) and Clean Architectural
principles.

This is a Java/Spring Boot rewrite of the [.NET counterpart](https://github.com/OneJackLee/DomainDrivenLibrary).

This simple web API server models a simple library system where:

- Borrowers (a.k.a. the library member) can register and borrow books
- Books existed as a physical copies that can be tracked and borrowed in this system
- Catalog contain metadata about books (ISBN, title, author), thus a catalog has one-to-many relationships with Books
    - ISBN served as the unique identifier for multiple physical copies

## Prerequisite

- [Install Java 17 (LTS)](https://adoptium.net/temurin/releases/?version=17)
- [Install Docker Engine](https://docs.docker.com/engine/install/)

## Get Started

### Build and host with Docker

1. Open your terminal, and clone the repository to your device.
    ```zsh
   git clone {the repository link}
   ```
2. Switch the directory to the root folder of this repository with the following command.
    ```zsh
    cd DomainDrivenLibraryV2
   ```
3. On the root folder of this repository, create all the necessary containers in your Docker by running the command
   below.
    - For macOS or Linux user, run `docker-compose up --build`.
    - For Windows user, run `docker-compose -f compose.windows.yaml up --build`.
4. Open your browser, and navigate
   to [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) for the Swagger UI.

### Build and run locally (without Docker)

1. Start a PostgreSQL database on port 5432 with database name `library_db`, username `postgres`, and password
   `postgres`.
2. Run the application using Maven:
    ```bash
    ./mvnw spring-boot:run -pl presentation
    ```
3. Open your browser, and navigate
   to [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) for the Swagger UI.

## Introduction to the Web API Server

See [API_Guide.md](./API_Guide.md) for detailed guide on how to use this API.

### API Summary

| # | Endpoint             | Method | URL                           | Description               |
|---|----------------------|--------|-------------------------------|---------------------------|
| 1 | Register Book        | POST   | `/api/books`                  | Register a new book copy  |
| 2 | Get All Books        | GET    | `/api/books`                  | Get all books             |
| 3 | Borrow Book          | POST   | `/api/books/{bookId}/borrow`  | Borrow a book             |
| 4 | Return Book          | POST   | `/api/books/{bookId}/return`  | Return a borrowed book    |
| 5 | Get Catalog Entry    | GET    | `/api/catalog-entries/{isbn}` | Get catalog entry by ISBN |
| 6 | Update Catalog Entry | PUT    | `/api/catalog-entries/{isbn}` | Update catalog entry      |
| 7 | Register Borrower    | POST   | `/api/borrowers`              | Register a new borrower   |
| 8 | Get All Borrowers    | GET    | `/api/borrowers`              | Get all borrowers         |

### Assumptions

This project was built with the following assumptions:

- **Scope**: Backend API only - no UI or authentication required
- **Borrower identity**: Email address uniquely identifies a borrower
- **ISBN enforcement**: Books with mismatched title/author for an existing ISBN are rejected
- **Pagination**: Not required for GetAll endpoints

See [Assumption.md](./Assumption.md) for the complete list including development process assumptions.

### Limitations

- The checksum for ISBN-10/ISBN-13 is not implemented yet
- The book borrow history is not implemented yet
- Integration tests are deferred due to Spring Boot 4.0 modularization changes

## Testing

- Domain Tests - Aggregate invariants, value object validation
- Application Tests - Handler logic with mocked dependencies

```bash
# Run all tests
./mvnw test -pl domain,application

# Run with verbosity
./mvnw test -pl domain,application -Dsurefire.printSummary=true
```

## Implementation Information

### Tech Stack

| Technology        | Version | Purpose          |
|-------------------|---------|------------------|
| Java              | 17 LTS  | Language         |
| Spring Boot       | 4.0.1   | Web Framework    |
| Spring Framework  | 7.0.2   | Core Framework   |
| Spring Data JPA   | -       | Data Access      |
| Hibernate         | 7.x     | ORM              |
| Flyway            | -       | DB Migration     |
| PostgreSQL        | 18.1    | Database         |
| ULID Creator      | 5.2.3   | ID Generation    |
| JUnit 5           | -       | Testing          |
| Mockito           | -       | Mocking          |
| AssertJ           | -       | Assertions       |
| SpringDoc OpenAPI | 2.8.x   | API Docs         |
| Docker            | -       | Containerization |

### How This Repository Was Built

To be transparent, the development process (as in the rewrite of the .NET application) was driven by Claude Code:

- I provide the system design, domain modeling, and architectural specifications.
  I also code the base implementation such as the domain classes, and command/query handlers.
- Claude Code contributed by migrating the existing code in the previous repository (check out
  the [Java_17_Migration.md](./Java_17_Migration.md) for more information),
  functioning as an intern/junior engineer under my direction.

The reason for this workflow is to maximize efficiency:

- I focus on high-value design and implementation.
- Routine or repetitive base coding tasks are shifted to AI assistance.
- Work proceeds in parallel, improving productivity by allowing me and the agent to operate concurrently.

#### About the Design Documents

The [Java_17_Migration.md](./Java_17_Migration.md) was created through a collaborative conversation with Claude Code (
Opus 4.5),
where I drove the migration approach while Claude helped structure and document them.

See [Post-Migration.md](./Post-Migration.md) for the post-migration report.

## Who do I talk to?

Please speak to [OneJackLee](mailto:charliewanj@outlook.com) should you have any issue.
