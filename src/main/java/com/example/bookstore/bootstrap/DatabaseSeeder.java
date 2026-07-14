package com.example.bookstore.bootstrap;

import com.example.bookstore.entity.Author;
import com.example.bookstore.entity.Book;
import com.example.bookstore.repository.AuthorRepository;
import com.example.bookstore.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public DatabaseSeeder(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (authorRepository.count() == 0 && bookRepository.count() == 0) {
            Author joshua = new Author("Joshua Bloch", "Joshua J. Bloch is an American software engineer and a former Sun Microsystems employee who led the design and implementation of numerous Java platform features.");
            Author martin = new Author("Robert C. Martin", "Robert Cecil Martin, colloquially known as Uncle Bob, is an American software engineer, instructor, and author.");
            
            authorRepository.save(joshua);
            authorRepository.save(martin);

            Book effectiveJava = new Book("Effective Java", joshua, "978-0134685991", 45.0);
            Book cleanCode = new Book("Clean Code", martin, "978-0132350884", 40.0);

            bookRepository.save(effectiveJava);
            bookRepository.save(cleanCode);

            System.out.println("Database successfully seeded with default authors and books.");
        }
    }
}
