package com.example.bookstore.service;

import com.example.bookstore.dto.AuthorRequest;
import com.example.bookstore.entity.Author;
import com.example.bookstore.exception.ResourceNotFoundException;
import com.example.bookstore.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author create(AuthorRequest request) {
        Author author = new Author(request.getName(), request.getBiography());
        return authorRepository.save(author);
    }

    public List<Author> getAll() {
        return authorRepository.findAll();
    }

    public Author getById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author with id " + id + " not found"));
    }

    public Author update(Long id, AuthorRequest request) {
        Author author = getById(id);
        author.setName(request.getName());
        author.setBiography(request.getBiography());
        return authorRepository.save(author);
    }

    public void delete(Long id) {
        Author author = getById(id);
        authorRepository.delete(author);
    }
}
