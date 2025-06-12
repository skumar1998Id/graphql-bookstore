package com.example.graphqlbookstore.repository;

import com.example.graphqlbookstore.model.Book;
import com.example.graphqlbookstore.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    /**
     * Find a book by ISBN
     * 
     * @param isbn the ISBN to search for
     * @return an Optional containing the book if found
     */
    Optional<Book> findByIsbn(String isbn);
    
    /**
     * Find books by title containing the given string (case insensitive)
     * 
     * @param title the title to search for
     * @return a list of books with titles containing the given string
     */
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Find books by author containing the given string (case insensitive)
     * 
     * @param author the author to search for
     * @return a list of books with authors containing the given string
     */
    List<Book> findByAuthorContainingIgnoreCase(String author);
    
    /**
     * Find books by category
     * 
     * @param category the category to search for
     * @return a list of books in the given category
     */
    List<Book> findByCategory(Category category);
    
    /**
     * Find books by publication year
     * 
     * @param year the publication year to search for
     * @return a list of books published in the given year
     */
    List<Book> findByPublicationYear(Integer year);
    
    /**
     * Check if a book exists with the given ISBN
     * 
     * @param isbn the ISBN to check
     * @return true if a book exists with the ISBN, false otherwise
     */
    boolean existsByIsbn(String isbn);
}
