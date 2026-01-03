# DomainDrivenLibraryV2

Domain Driven Library is a sample library management system with Domain-Driven Design (DDD) and Clean Architectural
principles.

This is a rewrite of the dotnet counterparts.

This simple web API server models a simple library system where:

- Borrowers (a.k.a. the library member) can register and borrow books
- Books existed as a physical copies that can be tracked and borrowed in this system
- Catalog contain metadata about books (ISBN, title, author), thus a catalog has one-to-many relationships with Books
    - ISBN served as the unique identifier for multiple physical copies
