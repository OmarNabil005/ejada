package com.example.bookstore.controller;

import com.example.bookstore.dto.AuthorRequest;
import com.example.bookstore.entity.Author;
import com.example.bookstore.repository.AuthorRepository;
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
class AuthorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Author existingAuthor;

    @BeforeEach
    void setUp() {
        authorRepository.deleteAll();
        authorRepository.flush();
        existingAuthor = new Author("Joshua Bloch", "Famous Java Developer");
        existingAuthor = authorRepository.save(existingAuthor);
    }

    @Test
    void testCreateAuthor_Success() throws Exception {
        AuthorRequest request = new AuthorRequest("Martin Fowler", "Author of Refactoring book");

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Martin Fowler"))
                .andExpect(jsonPath("$.biography").value("Author of Refactoring book"));
    }

    @Test
    void testCreateAuthor_InvalidName_BadRequest() throws Exception {
        AuthorRequest request = new AuthorRequest("", "Biography");

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("Name is required"));
    }

    @Test
    void testGetAllAuthors_Success() throws Exception {
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Joshua Bloch"));
    }

    @Test
    void testGetAuthorById_Success() throws Exception {
        mockMvc.perform(get("/api/authors/{id}", existingAuthor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Joshua Bloch"));
    }

    @Test
    void testGetAuthorById_NotFound() throws Exception {
        mockMvc.perform(get("/api/authors/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Author with id 999 not found"));
    }

    @Test
    void testUpdateAuthor_Success() throws Exception {
        AuthorRequest request = new AuthorRequest("Joshua Bloch (Updated)", "Updated Biography");

        mockMvc.perform(put("/api/authors/{id}", existingAuthor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Joshua Bloch (Updated)"))
                .andExpect(jsonPath("$.biography").value("Updated Biography"));
    }

    @Test
    void testUpdateAuthor_NotFound() throws Exception {
        AuthorRequest request = new AuthorRequest("Joshua Bloch (Updated)", "Updated Biography");

        mockMvc.perform(put("/api/authors/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Author with id 999 not found"));
    }

    @Test
    void testDeleteAuthor_Success() throws Exception {
        mockMvc.perform(delete("/api/authors/{id}", existingAuthor.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/authors/{id}", existingAuthor.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteAuthor_NotFound() throws Exception {
        mockMvc.perform(delete("/api/authors/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Author with id 999 not found"));
    }
}
