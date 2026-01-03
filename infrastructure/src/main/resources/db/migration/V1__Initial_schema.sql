-- V1__Initial_schema.sql
-- Initial database schema for Domain Driven Library

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
CREATE INDEX idx_borrowers_email ON borrowers(email_address);
