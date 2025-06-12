package com.example.graphqlbookstore.service;

import com.example.graphqlbookstore.model.Book;
import com.example.graphqlbookstore.model.Category;
import com.example.graphqlbookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Get all books
     *
     * @return a list of all books
     */
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Get a book by ID
     *
     * @param id the ID of the book to find
     * @return an Optional containing the book if found
     */
    @Transactional(readOnly = true)
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    /**
     * Get a book by ISBN
     *
     * @param isbn the ISBN of the book to find
     * @return an Optional containing the book if found
     */
    @Transactional(readOnly = true)
    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    /**
     * Get books by title (partial match, case insensitive)
     *
     * @param title the title to search for
     * @return a list of books with titles containing the given string
     */
    @Transactional(readOnly = true)
    public List<Book> getBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * Get books by author (partial match, case insensitive)
     *
     * @param author the author to search for
     * @return a list of books with authors containing the given string
     */
    @Transactional(readOnly = true)
    public List<Book> getBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    /**
     * Get books by category
     *
     * @param category the category to search for
     * @return a list of books in the given category
     */
    @Transactional(readOnly = true)
    public List<Book> getBooksByCategory(Category category) {
        return bookRepository.findByCategory(category);
    }

    /**
     * Create a new book
     *
     * @param book the book to create
     * @return the created book
     * @throws IllegalArgumentException if a book with the same ISBN already exists
     */
    @Transactional
    public Book createBook(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("A book with ISBN " + book.getIsbn() + " already exists");
        }
        return bookRepository.save(book);
    }

    /**
     * Update an existing book
     *
     * @param id the ID of the book to update
     * @param bookDetails the updated book details
     * @return an Optional containing the updated book if found
     * @throws IllegalArgumentException if updating the ISBN to one that already exists
     */
    @Transactional
    public Optional<Book> updateBook(Long id, Book bookDetails) {
        return bookRepository.findById(id).map(existingBook -> {
            // Check if ISBN is being changed and if the new ISBN already exists
            if (bookDetails.getIsbn() != null && 
                !existingBook.getIsbn().equals(bookDetails.getIsbn()) && 
                bookRepository.existsByIsbn(bookDetails.getIsbn())) {
                throw new IllegalArgumentException("A book with ISBN " + bookDetails.getIsbn() + " already exists");
            }
            
            // Update fields if they are not null
            if (bookDetails.getTitle() != null) {
                existingBook.setTitle(bookDetails.getTitle());
            }
            if (bookDetails.getAuthor() != null) {
                existingBook.setAuthor(bookDetails.getAuthor());
            }
            if (bookDetails.getDescription() != null) {
                existingBook.setDescription(bookDetails.getDescription());
            }
            if (bookDetails.getIsbn() != null) {
                existingBook.setIsbn(bookDetails.getIsbn());
            }
            if (bookDetails.getPrice() != null) {
                existingBook.setPrice(bookDetails.getPrice());
            }
            if (bookDetails.getStockQuantity() != null) {
                existingBook.setStockQuantity(bookDetails.getStockQuantity());
            }
            if (bookDetails.getCategory() != null) {
                existingBook.setCategory(bookDetails.getCategory());
            }
            if (bookDetails.getPublisher() != null) {
                existingBook.setPublisher(bookDetails.getPublisher());
            }
            if (bookDetails.getPublicationYear() != null) {
                existingBook.setPublicationYear(bookDetails.getPublicationYear());
            }
            if (bookDetails.getLanguage() != null) {
                existingBook.setLanguage(bookDetails.getLanguage());
            }
            if (bookDetails.getPageCount() != null) {
                existingBook.setPageCount(bookDetails.getPageCount());
            }
            
            return bookRepository.save(existingBook);
        });
    }

    /**
     * Delete a book by ID
     *
     * @param id the ID of the book to delete
     * @return true if the book was deleted, false if the book was not found
     */
    @Transactional
    public boolean deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Update the stock quantity of a book
     *
     * @param id the ID of the book to update
     * @param quantity the quantity to add (positive) or subtract (negative)
     * @return an Optional containing the updated book if found
     * @throws IllegalArgumentException if the resulting stock quantity would be negative
     */
    @Transactional
    public Optional<Book> updateBookStock(Long id, Integer quantity) {
        return bookRepository.findById(id).map(book -> {
            int newQuantity = book.getStockQuantity() + quantity;
            if (newQuantity < 0) {
                throw new IllegalArgumentException("Cannot reduce stock below zero");
            }
            book.setStockQuantity(newQuantity);
            return bookRepository.save(book);
        });
    }
}
