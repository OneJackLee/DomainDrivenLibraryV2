# Java 17 & Spring Boot Migration Plan

A comprehensive plan for rewriting Domain Driven Library from C#/.NET 9.0 to Java 17/Spring Boot.

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Java 17 vs Java 21 Considerations](#2-java-17-vs-java-21-considerations)
3. [Technology Stack Mapping](#3-technology-stack-mapping)
4. [Project Structure](#4-project-structure)
5. [Layer-by-Layer Migration](#5-layer-by-layer-migration)
6. [Key Code Examples](#6-key-code-examples)
7. [Testing Strategy](#7-testing-strategy)
8. [Build & Deployment](#8-build--deployment)
9. [Migration Phases](#9-migration-phases)
10. [Risk Assessment](#10-risk-assessment)
11. [Appendix: Spring Boot 4.0 Migration Notes](#appendix-spring-boot-40-migration-notes)

---

## 1. Executive Summary

### Current State
| Aspect | Technology |
|--------|------------|
| Language | C# 12 |
| Runtime | .NET 9.0 |
| Framework | ASP.NET Core 9.0 |
| ORM | Entity Framework Core 9.0 |
| Database | PostgreSQL |
| Architecture | DDD + Clean Architecture |

### Target State
| Aspect | Technology |
|--------|------------|
| Language | Java 17 (LTS) |
| Runtime | JVM 17 |
| Framework | Spring Boot 4.0.1 |
| Spring Framework | 7.0.2+ |
| ORM | Spring Data JPA + Hibernate 7.x |
| Database | PostgreSQL (unchanged) |
| Architecture | DDD + Clean Architecture (preserved) |
| Jakarta EE | 11 (Servlet 6.1) |

### Why Java 17 + Spring Boot 4.0.1?
- **Long-Term Support (LTS)** - Java 17 supported until September 2029
- **Spring Boot 4.x Compatibility** - Java 17 is the minimum requirement
- **Latest Spring Features** - Spring Framework 7, improved observability, better native support
- **Mature Feature Set** - Records, sealed classes, pattern matching for instanceof
- **Enterprise Adoption** - Widely adopted in enterprise environments
- **Future-Proof** - Easy upgrade path to Java 21/25 when ready

### Scope
- 3 Aggregates: Book, Borrower, CatalogEntry
- 8 REST API endpoints
- ~60 source files to migrate
- ~85 Java files to create

---

## 2. Java 17 vs Java 21 Considerations

### Features Available in Java 17

| Feature | Status | Usage in Migration |
|---------|--------|-------------------|
| Records | Finalized (Java 16) | Value objects, DTOs, Commands/Queries |
| Sealed Classes | Finalized (Java 17) | Domain type hierarchies |
| Pattern Matching for instanceof | Finalized (Java 16) | Type checking in domain logic |
| Text Blocks | Finalized (Java 15) | SQL queries, JSON templates |
| Switch Expressions | Finalized (Java 14) | Enum handling, mapping logic |
| Helpful NullPointerExceptions | Java 14+ | Better debugging |

### Features NOT Available in Java 17 (Java 21+)

| Feature | Alternative in Java 17 |
|---------|----------------------|
| Virtual Threads (Loom) | Traditional thread pools, `@Async` |
| Pattern Matching in Switch | If-else chains or visitor pattern |
| Record Patterns | Manual deconstruction |
| Sequenced Collections | LinkedHashMap/LinkedHashSet |
| String Templates | String.format() or MessageFormat |

### Impact on Migration
- **Minimal impact** - Core Spring Boot 4.0.1 features work identically with Java 17
- **Async handling** - Use `CompletableFuture` or `@Async` instead of virtual threads
- **Pattern matching** - Use traditional instanceof with cast
- **Future upgrade** - Can upgrade to Java 21/25 later for virtual threads without changing Spring Boot version

---

## 3. Technology Stack Mapping

### Framework Mapping

| .NET Component | Java 17/Spring Equivalent |
|----------------|---------------------------|
| ASP.NET Core 9.0 | Spring Boot 4.0.1 |
| Entity Framework Core | Spring Data JPA + Hibernate 7.x |
| Microsoft.Extensions.DI | Spring IoC Container |
| ASP.NET Controllers | @RestController |
| Swashbuckle | SpringDoc OpenAPI 2.8.x |
| ULID library | ulid-creator 5.x |

### Testing Framework Mapping

| .NET | Java 17 |
|------|---------|
| xUnit | JUnit 5 (Jupiter) |
| NSubstitute | Mockito 5.x |
| FluentAssertions | AssertJ 3.x |
| - | Testcontainers (integration) |

### C# to Java 17 Language Mapping

| C# Feature | Java 17 Equivalent |
|------------|-------------------|
| `record` | `record` |
| `sealed class` | `sealed class` |
| `init` properties | Constructor / Builder |
| `string?` (nullable) | `Optional<String>` or `@Nullable` |
| `async/await` | `CompletableFuture` or synchronous with `@Transactional` |
| LINQ | Stream API |
| `using` statement | try-with-resources |
| Extension methods | Static utility classes |
| Primary constructors | `@RequiredArgsConstructor` (Lombok) |
| `nameof()` | String literals or reflection |

---

## 4. Project Structure

### Current .NET Structure
```
DomainDrivenLibrary/
├── Common/
│   ├── DomainDrivenLibrary.Application.Abstractions/
│   └── DomainDrivenLibrary.Domain.Shared/
├── Sources/
│   ├── DomainDrivenLibrary.Domain/
│   ├── DomainDrivenLibrary.Application/
│   └── DomainDrivenLibrary.Infrastructure/
├── DomainDrivenLibrary.Presentation/
└── Tests/
```

### Target Java 17 Structure
```
domain-driven-library/
├── pom.xml                          # Parent POM (Java 17, Spring Boot 4.0.1)
├── common/
│   ├── pom.xml
│   └── src/main/java/com/library/common/
│       ├── domain/
│       │   ├── Entity.java          # Interface
│       │   └── AggregateRoot.java   # Abstract class
│       └── application/
│           ├── IdGenerator.java     # Interface
│           └── UnitOfWork.java      # Interface (optional)
├── domain/
│   ├── pom.xml
│   └── src/main/java/com/library/domain/
│       ├── book/
│       │   ├── Book.java            # Aggregate root
│       │   ├── BookId.java          # Value object (record)
│       │   ├── BookRepository.java  # Interface
│       │   └── BookWithCatalog.java # Projection record
│       ├── borrower/
│       │   ├── Borrower.java
│       │   ├── BorrowerId.java
│       │   ├── EmailAddress.java    # Value object (record)
│       │   └── BorrowerRepository.java
│       └── catalogentry/
│           ├── CatalogEntry.java
│           ├── Isbn.java            # Value object (record)
│           └── CatalogEntryRepository.java
├── application/
│   ├── pom.xml
│   └── src/main/java/com/library/application/
│       ├── book/
│       │   ├── RegisterBookCommand.java
│       │   ├── RegisterBookCommandHandler.java
│       │   ├── BorrowBookCommand.java
│       │   ├── BorrowBookCommandHandler.java
│       │   ├── ReturnBookCommand.java
│       │   ├── ReturnBookCommandHandler.java
│       │   ├── GetAllBooksQuery.java
│       │   ├── GetAllBooksQueryHandler.java
│       │   └── BookDetailsDto.java
│       ├── borrower/
│       │   ├── RegisterBorrowerCommand.java
│       │   ├── RegisterBorrowerCommandHandler.java
│       │   ├── GetAllBorrowersQuery.java
│       │   ├── GetAllBorrowersQueryHandler.java
│       │   └── BorrowerDto.java
│       ├── catalogentry/
│       │   ├── GetCatalogEntryByIsbnQuery.java
│       │   ├── GetCatalogEntryByIsbnQueryHandler.java
│       │   ├── UpdateCatalogEntryCommand.java
│       │   ├── UpdateCatalogEntryCommandHandler.java
│       │   └── CatalogEntryDto.java
│       └── exception/
│           ├── BookNotFoundException.java
│           ├── BorrowerNotFoundException.java
│           ├── CatalogEntryNotFoundException.java
│           └── CatalogEntryConflictException.java
├── infrastructure/
│   ├── pom.xml
│   └── src/main/java/com/library/infrastructure/
│       ├── persistence/
│       │   ├── entity/
│       │   │   ├── BookJpaEntity.java
│       │   │   ├── BorrowerJpaEntity.java
│       │   │   └── CatalogEntryJpaEntity.java
│       │   ├── repository/
│       │   │   ├── BookJpaRepository.java      # Spring Data interface
│       │   │   ├── BookRepositoryImpl.java     # Adapter
│       │   │   ├── BorrowerJpaRepository.java
│       │   │   ├── BorrowerRepositoryImpl.java
│       │   │   ├── CatalogEntryJpaRepository.java
│       │   │   └── CatalogEntryRepositoryImpl.java
│       │   └── converter/
│       │       └── (JPA AttributeConverters if needed)
│       ├── identifier/
│       │   └── UlidGenerator.java
│       └── config/
│           └── JpaConfig.java
├── presentation/
│   ├── pom.xml
│   └── src/main/java/com/library/presentation/
│       ├── LibraryApplication.java  # @SpringBootApplication
│       ├── controller/
│       │   ├── BooksController.java
│       │   ├── BorrowersController.java
│       │   └── CatalogEntriesController.java
│       ├── dto/
│       │   ├── request/
│       │   │   ├── RegisterBookRequest.java
│       │   │   ├── BorrowBookRequest.java
│       │   │   ├── ReturnBookRequest.java
│       │   │   ├── RegisterBorrowerRequest.java
│       │   │   └── UpdateCatalogEntryRequest.java
│       │   └── response/
│       │       ├── BookResponse.java
│       │       ├── BorrowerResponse.java
│       │       ├── CatalogEntryResponse.java
│       │       └── ErrorResponse.java
│       └── exception/
│           └── GlobalExceptionHandler.java
└── src/
    └── test/java/com/library/
        ├── domain/          # Domain unit tests
        └── application/     # Handler unit tests
```

---

## 5. Layer-by-Layer Migration

### 5.1 Common Module

| C# File | Java 17 File | Notes |
|---------|--------------|-------|
| `IEntity.cs` | `Entity.java` | Interface with `getId()` |
| `EntityBase.cs` | `AggregateRoot.java` | Abstract class, `@MappedSuperclass` |
| `IIdGenerator.cs` | `IdGenerator.java` | Interface |
| `IUnitOfWork.cs` | `UnitOfWork.java` | Optional (Spring `@Transactional` handles this) |
| `IScopedDependency.cs` | Not needed | Spring uses `@Service`, `@Repository` |

### 5.2 Domain Module

#### Value Objects (as Java Records)

| C# File | Java 17 File | Validation |
|---------|--------------|------------|
| `Isbn.cs` | `Isbn.java` | 10 or 13 digits, normalization |
| `BookId.cs` | `BookId.java` | Non-blank ULID |
| `BorrowerId.cs` | `BorrowerId.java` | Non-blank ULID |
| `EmailAddress.cs` | `EmailAddress.java` | Email format validation |

#### Aggregates

| C# File | Java 17 File | Methods |
|---------|--------------|---------|
| `Book.cs` | `Book.java` | `register()`, `borrow()`, `returnBook()` |
| `Borrower.cs` | `Borrower.java` | `register()`, `updateName()`, `updateEmailAddress()` |
| `CatalogEntry.cs` | `CatalogEntry.java` | `create()`, `updateTitle()`, `updateAuthor()` |

#### Repository Interfaces

| C# File | Java 17 File |
|---------|--------------|
| `IBookRepository.cs` | `BookRepository.java` |
| `IBorrowerRepository.cs` | `BorrowerRepository.java` |
| `ICatalogEntryRepository.cs` | `CatalogEntryRepository.java` |

### 5.3 Application Module

#### Commands & Handlers

| Use Case | Command | Handler |
|----------|---------|---------|
| Register Book | `RegisterBookCommand` | `RegisterBookCommandHandler` |
| Borrow Book | `BorrowBookCommand` | `BorrowBookCommandHandler` |
| Return Book | `ReturnBookCommand` | `ReturnBookCommandHandler` |
| Register Borrower | `RegisterBorrowerCommand` | `RegisterBorrowerCommandHandler` |
| Update Catalog | `UpdateCatalogEntryCommand` | `UpdateCatalogEntryCommandHandler` |

#### Queries & Handlers

| Use Case | Query | Handler |
|----------|-------|---------|
| Get All Books | `GetAllBooksQuery` | `GetAllBooksQueryHandler` |
| Get All Borrowers | `GetAllBorrowersQuery` | `GetAllBorrowersQueryHandler` |
| Get Catalog Entry | `GetCatalogEntryByIsbnQuery` | `GetCatalogEntryByIsbnQueryHandler` |

### 5.4 Infrastructure Module

#### JPA Entities (separate from domain entities)

| Domain Entity | JPA Entity | Notes |
|---------------|------------|-------|
| `Book` | `BookJpaEntity` | Maps to `books` table |
| `Borrower` | `BorrowerJpaEntity` | Maps to `borrowers` table |
| `CatalogEntry` | `CatalogEntryJpaEntity` | Maps to `catalog_entries` table |

#### Repository Implementations

| Interface | Spring Data | Adapter |
|-----------|-------------|---------|
| `BookRepository` | `BookJpaRepository` | `BookRepositoryImpl` |
| `BorrowerRepository` | `BorrowerJpaRepository` | `BorrowerRepositoryImpl` |
| `CatalogEntryRepository` | `CatalogEntryJpaRepository` | `CatalogEntryRepositoryImpl` |

### 5.5 Presentation Module

#### Controllers

| C# Controller | Java Controller | Endpoints |
|---------------|-----------------|-----------|
| `BooksController` | `BooksController` | POST /, GET /, POST /{id}/borrow, POST /{id}/return |
| `BorrowersController` | `BorrowersController` | POST /, GET / |
| `CatalogEntriesController` | `CatalogEntriesController` | GET /{isbn}, PUT /{isbn} |

---

## 6. Key Code Examples

### 6.1 Value Object (Record)

```java
// Isbn.java
public record Isbn(String value) {

    private static final Pattern ISBN_10 = Pattern.compile("^\\d{9}[\\dX]$");
    private static final Pattern ISBN_13 = Pattern.compile("^\\d{13}$");

    public Isbn {
        Objects.requireNonNull(value, "ISBN cannot be null");
        value = normalize(value);
        if (!isValid(value)) {
            throw new IllegalArgumentException(
                "ISBN must be 10 or 13 digits. Got: " + value);
        }
    }

    public static Isbn create(String value) {
        return new Isbn(value);
    }

    public static Optional<Isbn> tryParse(String value) {
        try {
            return Optional.of(new Isbn(value));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static String normalize(String value) {
        return value.replace("-", "").replace(" ", "").toUpperCase();
    }

    private static boolean isValid(String value) {
        return ISBN_10.matcher(value).matches() || ISBN_13.matcher(value).matches();
    }
}
```

### 6.2 Aggregate Root

```java
// Book.java
public class Book extends AggregateRoot<BookId> {

    private Isbn isbn;
    private BorrowerId borrowerId;
    private LocalDateTime borrowedOn;

    protected Book() {} // JPA

    private Book(BookId id, Isbn isbn) {
        super(id);
        this.isbn = Objects.requireNonNull(isbn);
    }

    public static Book register(BookId id, Isbn isbn) {
        return new Book(id, isbn);
    }

    public Book borrow(BorrowerId borrowerId) {
        return borrow(borrowerId, LocalDateTime.now(ZoneOffset.UTC));
    }

    public Book borrow(BorrowerId borrowerId, LocalDateTime borrowedOn) {
        if (!isAvailable()) {
            throw new IllegalStateException("Book is already borrowed");
        }
        this.borrowerId = Objects.requireNonNull(borrowerId);
        this.borrowedOn = borrowedOn;
        return this;
    }

    public Book returnBook() {
        if (isAvailable()) {
            throw new IllegalStateException("Book is not borrowed");
        }
        this.borrowerId = null;
        this.borrowedOn = null;
        return this;
    }

    public boolean isAvailable() { return borrowerId == null; }
    public Isbn getIsbn() { return isbn; }
    public Optional<BorrowerId> getBorrowerId() { return Optional.ofNullable(borrowerId); }
    public Optional<LocalDateTime> getBorrowedOn() { return Optional.ofNullable(borrowedOn); }
}
```

### 6.3 Command Handler

```java
// RegisterBookCommandHandler.java
@Service
@RequiredArgsConstructor
public class RegisterBookCommandHandler {

    private final BookRepository bookRepository;
    private final CatalogEntryRepository catalogEntryRepository;
    private final IdGenerator idGenerator;

    @Transactional
    public BookDetailsDto handle(RegisterBookCommand command) {
        Isbn isbn = Isbn.create(command.isbn());

        CatalogEntry catalogEntry = catalogEntryRepository.findByIsbn(isbn)
            .map(existing -> validateAndReturn(existing, command))
            .orElseGet(() -> createCatalogEntry(isbn, command));

        BookId bookId = BookId.create(idGenerator.generate());
        Book book = Book.register(bookId, isbn);
        bookRepository.save(book);

        return BookDetailsDto.from(book, catalogEntry);
    }

    private CatalogEntry validateAndReturn(CatalogEntry existing, RegisterBookCommand cmd) {
        if (!existing.getTitle().equalsIgnoreCase(cmd.title()) ||
            !existing.getAuthor().equalsIgnoreCase(cmd.author())) {
            throw new CatalogEntryConflictException(
                "ISBN exists with different metadata");
        }
        return existing;
    }

    private CatalogEntry createCatalogEntry(Isbn isbn, RegisterBookCommand cmd) {
        CatalogEntry entry = CatalogEntry.create(isbn, cmd.title(), cmd.author());
        catalogEntryRepository.save(entry);
        return entry;
    }
}
```

### 6.4 REST Controller

```java
// BooksController.java
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Books")
public class BooksController {

    private final RegisterBookCommandHandler registerHandler;
    private final GetAllBooksQueryHandler getAllHandler;
    private final BorrowBookCommandHandler borrowHandler;
    private final ReturnBookCommandHandler returnHandler;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse register(@Valid @RequestBody RegisterBookRequest request) {
        var command = new RegisterBookCommand(request.isbn(), request.title(), request.author());
        return BookResponse.from(registerHandler.handle(command));
    }

    @GetMapping
    public List<BookResponse> getAll() {
        return getAllHandler.handle(new GetAllBooksQuery()).stream()
            .map(BookResponse::from)
            .toList();
    }

    @PostMapping("/{bookId}/borrow")
    public BookResponse borrow(@PathVariable String bookId,
                               @Valid @RequestBody BorrowBookRequest request) {
        var command = new BorrowBookCommand(bookId, request.borrowerId());
        return BookResponse.from(borrowHandler.handle(command));
    }

    @PostMapping("/{bookId}/return")
    public BookResponse returnBook(@PathVariable String bookId,
                                   @Valid @RequestBody ReturnBookRequest request) {
        var command = new ReturnBookCommand(bookId, request.borrowerId());
        return BookResponse.from(returnHandler.handle(command));
    }
}
```

### 6.5 Global Exception Handler

```java
// GlobalExceptionHandler.java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return new ErrorResponse("BAD_REQUEST", ex.getMessage());
    }

    @ExceptionHandler({BookNotFoundException.class, BorrowerNotFoundException.class,
                       CatalogEntryNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(RuntimeException ex) {
        return new ErrorResponse("NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(CatalogEntryConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(CatalogEntryConflictException ex) {
        return new ErrorResponse("CONFLICT", ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidState(IllegalStateException ex) {
        return new ErrorResponse("INVALID_STATE", ex.getMessage());
    }
}
```

---

## 7. Testing Strategy

### 7.1 Domain Unit Tests (JUnit 5 + AssertJ)

```java
@Nested
class BookBorrowTests {

    @Test
    void borrow_WhenAvailable_SetsBorrowerAndTimestamp() {
        Book book = Book.register(BOOK_ID, ISBN);

        book.borrow(BORROWER_ID);

        assertThat(book.isAvailable()).isFalse();
        assertThat(book.getBorrowerId()).hasValue(BORROWER_ID);
        assertThat(book.getBorrowedOn()).isPresent();
    }

    @Test
    void borrow_WhenAlreadyBorrowed_ThrowsException() {
        Book book = Book.register(BOOK_ID, ISBN);
        book.borrow(BORROWER_ID);

        assertThatThrownBy(() -> book.borrow(OTHER_BORROWER))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("already borrowed");
    }
}
```

### 7.2 Application Unit Tests (Mockito)

```java
@ExtendWith(MockitoExtension.class)
class RegisterBookCommandHandlerTests {

    @Mock private BookRepository bookRepository;
    @Mock private CatalogEntryRepository catalogEntryRepository;
    @Mock private IdGenerator idGenerator;
    @InjectMocks private RegisterBookCommandHandler handler;

    @Test
    void handle_WithNewIsbn_CreatesCatalogEntryAndBook() {
        when(idGenerator.generate()).thenReturn("01ABC");
        when(catalogEntryRepository.findByIsbn(any())).thenReturn(Optional.empty());

        var result = handler.handle(new RegisterBookCommand("9780132350884", "Clean Code", "Martin"));

        assertThat(result.isbn()).isEqualTo("9780132350884");
        verify(catalogEntryRepository).save(any());
        verify(bookRepository).save(any());
    }
}
```

### 7.3 Integration Tests (Testcontainers)

```java
@SpringBootTest
@Testcontainers
class BookIntegrationTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void registerBook_Returns201() throws Exception {
        var request = new RegisterBookRequest("9780132350884", "Clean Code", "Martin");

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.isbn").value("9780132350884"));
    }
}
```

---

## 8. Build & Deployment

### 8.1 Parent POM (Key Sections)

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.1</version>
</parent>

<properties>
    <java.version>17</java.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>

<dependencies>
    <!-- Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Database -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-database-postgresql</artifactId>
    </dependency>

    <!-- API Docs -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.8.0</version>
    </dependency>

    <!-- Utilities -->
    <dependency>
        <groupId>com.github.f4b6a3</groupId>
        <artifactId>ulid-creator</artifactId>
        <version>5.2.3</version>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

> **Note:** Spring Boot 4.0.1 uses Jakarta EE 11 with Servlet 6.1 baseline. The `flyway-database-postgresql`
> artifact is now required instead of `flyway-core` for PostgreSQL support.

### 8.2 Dockerfile

```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/presentation/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 8.3 Docker Compose

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/library
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: password
    depends_on:
      db:
        condition: service_healthy

  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: library
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 5s
      timeout: 5s
      retries: 5

volumes:
  postgres_data:
```

### 8.4 Flyway Migration

```sql
-- V1__Initial_schema.sql
CREATE TABLE catalog_entries (
    isbn VARCHAR(13) PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    author VARCHAR(500) NOT NULL
);

CREATE TABLE borrowers (
    id VARCHAR(26) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email_address VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE books (
    id VARCHAR(26) PRIMARY KEY,
    isbn VARCHAR(13) NOT NULL REFERENCES catalog_entries(isbn),
    borrower_id VARCHAR(26) REFERENCES borrowers(id),
    borrowed_on TIMESTAMP
);

CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_books_borrower_id ON books(borrower_id);
```

---

## 9. Migration Phases

### Phase 1: Project Setup (Day 1 Morning)
- [ ] Create Maven multi-module project
- [ ] Configure parent POM with Java 17
- [ ] Add all dependencies
- [ ] Set up Docker Compose for PostgreSQL
- [ ] Create `application.yml` configuration

### Phase 2: Common Module (Day 1 Morning)
- [ ] `Entity.java` interface
- [ ] `AggregateRoot.java` abstract class
- [ ] `IdGenerator.java` interface

### Phase 3: Domain Module (Day 1 Afternoon)
- [ ] Value Objects: `Isbn`, `BookId`, `BorrowerId`, `EmailAddress`
- [ ] Aggregates: `Book`, `Borrower`, `CatalogEntry`
- [ ] Repository interfaces
- [ ] `BookWithCatalog` projection

### Phase 4: Application Module (Day 1 Evening)
- [ ] All Command records
- [ ] All Query records
- [ ] All Handler classes
- [ ] DTOs: `BookDetailsDto`, `BorrowerDto`, `CatalogEntryDto`
- [ ] Custom exceptions

### Phase 5: Infrastructure Module (Day 2 Morning)
- [ ] JPA entities with mappers
- [ ] Spring Data repositories
- [ ] Repository implementations (adapters)
- [ ] `UlidGenerator` implementation
- [ ] JPA configuration
- [ ] Flyway migration script

### Phase 6: Presentation Module (Day 2 Afternoon)
- [ ] `LibraryApplication` main class
- [ ] All request DTOs
- [ ] All response DTOs
- [ ] `BooksController`
- [ ] `BorrowersController`
- [ ] `CatalogEntriesController`
- [ ] `GlobalExceptionHandler`

### Phase 7: Testing (Day 2 Evening)
- [ ] Domain unit tests
- [ ] Handler unit tests
- [ ] Integration tests with Testcontainers

### Phase 8: Validation
- [ ] `mvn compile` succeeds
- [ ] `mvn test` passes
- [ ] Application starts successfully
- [ ] All 8 API endpoints work via Swagger UI
- [ ] Docker build succeeds

---

## 10. Risk Assessment

| Risk | Impact | Likelihood | Mitigation |
|------|--------|------------|------------|
| Spring Boot learning curve | Medium | Medium | Follow existing patterns, use Lombok |
| JPA entity mapping errors | Medium | Medium | Keep JPA entities separate from domain |
| API behavior differences | High | Low | Comprehensive integration tests |
| Transaction handling | Medium | Low | Use `@Transactional` on handlers |
| Null safety differences | Low | Medium | Use `Optional` consistently |
| Build configuration issues | Medium | Medium | Copy Maven config from this plan |

---

## API Endpoints (Unchanged)

| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|--------------|
| POST | `/api/books` | Register a new book | 201, 400, 409 |
| GET | `/api/books` | Get all books | 200 |
| POST | `/api/books/{bookId}/borrow` | Borrow a book | 200, 400, 404 |
| POST | `/api/books/{bookId}/return` | Return a book | 200, 400, 404 |
| GET | `/api/catalog-entries/{isbn}` | Get catalog entry | 200, 400, 404 |
| PUT | `/api/catalog-entries/{isbn}` | Update catalog entry | 200, 400, 404 |
| POST | `/api/borrowers` | Register borrower | 201, 400, 409 |
| GET | `/api/borrowers` | Get all borrowers | 200 |

---

## File Count Summary

| Module | Files | Estimated Effort |
|--------|-------|------------------|
| Common | 3 | 30 min |
| Domain | 12 | 2 hrs |
| Application | 20 | 3 hrs |
| Infrastructure | 12 | 2.5 hrs |
| Presentation | 14 | 2 hrs |
| Tests | 16 | 2.5 hrs |
| Config | 8 | 1.5 hrs |
| **Total** | **~85** | **~14 hrs** |

---

## Quick Reference: Pattern Mapping

| Pattern | C# | Java 17 |
|---------|-----|---------|
| Value Object | `record Isbn` | `record Isbn(String value)` |
| Nullable | `BorrowerId?` | `Optional<BorrowerId>` |
| DI Registration | `IScopedDependency` | `@Service` / `@Repository` |
| Async Handler | `async Task<T>` | `@Transactional T handle()` |
| Exception Middleware | `ExceptionHandlingMiddleware` | `@RestControllerAdvice` |
| Unit of Work | `IUnitOfWork.SaveChangesAsync()` | `@Transactional` (automatic) |
| Primary Constructor | `class Foo(IBar bar)` | `@RequiredArgsConstructor` |

---

## Appendix: Spring Boot 4.0 Migration Notes

### Key Changes from Spring Boot 3.x

| Change | Impact |
|--------|--------|
| Jakarta EE 11 | Same as 3.x, uses `jakarta.*` packages |
| Servlet 6.1 | Updated servlet container requirements |
| Hibernate 7.x | Minor API changes, improved performance |
| Spring Framework 7 | Enhanced observability, better RestClient |
| Flyway 11+ | Use `flyway-database-postgresql` for PostgreSQL |

### Breaking Changes to Watch

1. **Flyway artifact change**: Use `flyway-database-postgresql` instead of `flyway-core`
2. **Some deprecated APIs removed**: Check Spring Boot 4.0 Migration Guide
3. **Minimum Tomcat 11**: Embedded Tomcat upgraded to version 11

### Resources

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [Spring Boot 4.0 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes)
- [Spring Framework 7 What's New](https://www.baeldung.com/spring-boot-4-spring-framework-7)

---

*Document Version: 1.1*
*Target: Java 17 LTS + Spring Boot 4.0.1*
*Last Updated: January 2026*
