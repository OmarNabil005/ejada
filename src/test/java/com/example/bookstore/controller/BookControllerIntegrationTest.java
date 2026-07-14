package com.example.bookstore.controller;

import com.example.bookstore.dto.BookRequest;
import com.example.bookstore.entity.Author;
import com.example.bookstore.entity.Book;
import com.example.bookstore.repository.AuthorRepository;
import com.example.bookstore.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Author existingAuthor;
    private Book existingBook;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        bookRepository.flush();
        authorRepository.flush();
        
        existingAuthor = new Author("Joshua Bloch", "Java creator");
        existingAuthor = authorRepository.save(existingAuthor);
        
        existingBook = new Book("Effective Java", existingAuthor, "978-0134685991", 45.0);
        existingBook = bookRepository.save(existingBook);
    }

    @Test
    void testCreateBook_Success() throws Exception {
        Author otherAuthor = new Author("Robert C. Martin", "Uncle Bob");
        otherAuthor = authorRepository.save(otherAuthor);
        
        BookRequest request = new BookRequest("Clean Code", otherAuthor.getId(), "978-0132350884", 40.0);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.author.name").value("Robert C. Martin"))
                .andExpect(jsonPath("$.isbn").value("978-0132350884"))
                .andExpect(jsonPath("$.price").value(40.0));
    }

    @Test
    void testCreateBook_InvalidPayload_BadRequest() throws Exception {
        // Missing title, negative price
        BookRequest request = new BookRequest("", existingAuthor.getId(), "978-0132350884", -5.0);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors[*].field", containsInAnyOrder("title", "price")));
    }

    @Test
    void testCreateBook_AuthorNotFound_NotFound() throws Exception {
        BookRequest request = new BookRequest("Clean Code", 999L, "978-0132350884", 40.0);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Author with id 999 not found"));
    }

    @Test
    void testGetAllBooks_Success() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Effective Java"));
    }

    @Test
    void testGetBookById_Success() throws Exception {
        mockMvc.perform(get("/api/books/{id}", existingBook.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Effective Java"));
    }

    @Test
    void testGetBookById_NotFound() throws Exception {
        mockMvc.perform(get("/api/books/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book with id 999 not found"));
    }

    @Test
    void testUpdateBook_Success() throws Exception {
        BookRequest request = new BookRequest("Effective Java 3rd Edition", existingAuthor.getId(), "978-0134685991", 50.0);

        mockMvc.perform(put("/api/books/{id}", existingBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Effective Java 3rd Edition"))
                .andExpect(jsonPath("$.price").value(50.0));
    }

    @Test
    void testUpdateBook_NotFound() throws Exception {
        BookRequest request = new BookRequest("Effective Java 3rd Edition", existingAuthor.getId(), "978-0134685991", 50.0);

        mockMvc.perform(put("/api/books/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book with id 999 not found"));
    }

    @Test
    void testDeleteBook_Success() throws Exception {
        mockMvc.perform(delete("/api/books/{id}", existingBook.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/books/{id}", existingBook.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteBook_NotFound() throws Exception {
        mockMvc.perform(delete("/api/books/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book with id 999 not found"));
    }

    @Test
    void testGetBookByIsbn_Success() throws Exception {
        mockMvc.perform(get("/api/books/isbn/{isbn}", existingBook.getIsbn()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Effective Java"));
    }

    @Test
    void testGetBookByIsbn_NotFound() throws Exception {
        mockMvc.perform(get("/api/books/isbn/{isbn}", "non-existent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book with ISBN non-existent not found"));
    }
}
