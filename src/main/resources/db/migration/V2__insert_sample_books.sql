-- Insert Sample Books
INSERT INTO books (title, author, price, stock_quantity, isbn, category, created_at, updated_at)
VALUES
    ('Spring Boot in Action', 'Craig Walls', 30000, 100, '9781617292545', 'IT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Clean Code', 'Robert C. Martin', 25000, 50, '9780132350884', 'IT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('The Pragmatic Programmer', 'Andrew Hunt', 28000, 70, '9780201616224', 'IT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Domain-Driven Design', 'Eric Evans', 35000, 30, '9780321125217', 'IT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Refactoring', 'Martin Fowler', 32000, 40, '9780201485677', 'IT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
