# Ejada Bookstore Coding Assignment

This repository contains my implementation of the **Bookstore Assignment** for the Ejada application process. It is a Java-based RESTful API built using **Spring Boot**, **Spring Data JPA**, and an in-memory **H2 database**.

Below is a detailed guide of what has been implemented across the three phases, how the codebase is structured, and how you can run and test the application on your machine.

---

## 🛠️ Tech Stack Used

* **Java 21**
* **Spring Boot 4.1.0** (with starter packs for Web, Validation, and JPA)
* **H2 Database** (In-memory database for testing and local runs)
* **Maven** (Project build and dependency management)

---

## 📂 How the Code is Structured

I organized the codebase into a clean, layered structure to keep concerns separated:

* **`com.example.bookstore`**: The main package containing the entry point `BookstoreApplication.java`.
* **`entity`**: Holds the JPA entities representing our database schema (`Book` and `Author`).
* **`repository`**: Houses our Spring Data JPA interfaces (`BookRepository` and `AuthorRepository`) which handle database interactions.
* **`service`**: Implements the business logic layer (`BookService` and `AuthorService`) to keep the controllers thin and focused.
* **`controller`**: Exposes the REST endpoints (`BookController` and `AuthorController`). As recommended in the guidelines, I used **constructor-based dependency injection** here instead of field injection.
* **`dto`**: Contains DTOs (`BookRequest` and `AuthorRequest`) to validate incoming payloads before they reach the persistence layers.
* **`exception`**: Holds the custom `ResourceNotFoundException`.
* **`advice`**: Houses the `GlobalExceptionHandler` to cleanly catch and structure validation errors and missing resource exceptions.

---

## 📝 Implementation Walkthrough

Here is how I tackled the three phases of the assignment:

### Phase 1: Core CRUD Operations for Books
* Developed the basic CRUD operations for managing books under `/api/books` (`POST`, `GET`, `PUT`, `DELETE`).
* Created a custom query finder method in the repository `findByIsbn(String isbn)` and exposed it on `GET /api/books/isbn/{isbn}` to look up books directly by their ISBN.
* Ensured correct HTTP responses (e.g., returning `204 No Content` for successful deletes and `404 Not Found` for missing resources on updates).

### Phase 2: Centralized Exception Handling
* Set up a dedicated global controller advice (`@RestControllerAdvice`) to handle exceptions across all controllers.
* Intercepted `MethodArgumentNotValidException` to capture field-level validation errors (such as negative book prices or empty names) and output them as a clean JSON structure instead of long, confusing stack traces.
* Configured the handler to catch the custom `ResourceNotFoundException` and respond with a neat `404 Not Found` error and a clear message.

### Phase 3: Relational Data Mapping (Books & Authors)
* Implemented the `Author` entity with `id`, `name`, and `biography`.
* Configured the JPA relationship so that a **Book** belongs to a single **Author** (`@ManyToOne`), while an **Author** can write multiple **Books** (`@OneToMany`).
* Added cascade and orphan removal so that deleting an author automatically cleans up their books.
* Created independent endpoints for authors at `/api/authors` to manage their lifecycles.

---

## 🌐 API Endpoint Summary

### Books (`/api/books`)
* **`POST /api/books`**: Creates a new book (requires validation).
* **`GET /api/books`**: Retrieves a list of all books.
* **`GET /api/books/{id}`**: Retrieves a book by its database ID.
* **`PUT /api/books/{id}`**: Updates an existing book.
* **`DELETE /api/books/{id}`**: Deletes a book and returns `204 No Content`.
* **`GET /api/books/isbn/{isbn}`**: Searches for a book using its ISBN.

**Example Request Payload (`POST /api/books`):**
```json
{
  "title": "Clean Code",
  "authorId": 1,
  "isbn": "978-0132350884",
  "price": 35.99
}
```

### Authors (`/api/authors`)
* **`POST /api/authors`**: Creates an author.
* **`GET /api/authors`**: Retrieves all authors.
* **`GET /api/authors/{id}`**: Retrieves a single author by ID.
* **`PUT /api/authors/{id}`**: Updates an author's info.
* **`DELETE /api/authors/{id}`**: Deletes an author.

**Example Request Payload (`POST /api/authors`):**
```json
{
  "name": "Robert C. Martin",
  "biography": "American software engineer and author commonly known as Uncle Bob."
}
```

---

## 🛡️ Input Validation Rules

* **Books:**
  * Title and ISBN cannot be blank.
  * Price must be positive (`@Positive`).
  * Author ID is required.
* **Authors:**
  * Name cannot be blank.

---

## 🚨 Error Response Examples

### Validation Failure (HTTP 400 Bad Request)
If you try to submit a book with a negative price or missing title, you will receive a clean error response listing the invalid fields:
```json
{
  "errors": [
    {
      "field": "price",
      "message": "Price must be positive"
    },
    {
      "field": "title",
      "message": "Title is required"
    }
  ]
}
```

### Resource Not Found (HTTP 404 Not Found)
If you try to request or update an ID that does not exist:
```json
{
  "message": "Book with id 99 not found"
}
```

---

## 🗄️ Database & Console
The application uses an in-memory H2 database, which makes it very lightweight and easy to run without any external database setup:
* **Console URL:** `http://localhost:8080/h2-console`
* **JDBC URL:** `jdbc:h2:mem:bookstore`
* **Username:** `sa`
* **Password:** *(leave blank)*

---

## ⚙️ How to Build and Run the App

You can compile, test, and run the project using the included Maven wrapper.

### Build and Package:
```bash
# Windows
.\mvnw.cmd clean package

# macOS/Linux
./mvnw clean package
```

### Start the Server:
```bash
# Windows
.\mvnw.cmd spring-boot:run

# macOS/Linux
./mvnw spring-boot:run
```

The application will start on port `8080`. You can access it at `http://localhost:8080`.
