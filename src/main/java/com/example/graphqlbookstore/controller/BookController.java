package com.example.graphqlbookstore.controller;

import com.example.graphqlbookstore.model.Book;
import com.example.graphqlbookstore.model.Category;
import com.example.graphqlbookstore.service.BookService;
import com.example.graphqlbookstore.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class BookController {

    private final BookService bookService;
    private final CategoryService categoryService;

    @Autowired
    public BookController(BookService bookService, CategoryService categoryService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
    }

    /**
     * GraphQL query to get all books
     *
     * @return a list of all books
     */
    @QueryMapping
    public List<Book> books() {
        return bookService.getAllBooks();
    }

    /**
     * GraphQL query to get a book by ID
     *
     * @param id the ID of the book to find
     * @return the book if found, null otherwise
     */
    @QueryMapping
    public Book book(@Argument Long id) {
        return bookService.getBookById(id).orElse(null);
    }

    /**
     * GraphQL query to get a book by ISBN
     *
     * @param isbn the ISBN of the book to find
     * @return the book if found, null otherwise
     */
    @QueryMapping
    public Book bookByIsbn(@Argument String isbn) {
        return bookService.getBookByIsbn(isbn).orElse(null);
    }

    /**
     * GraphQL query to get books by title (partial match, case insensitive)
     *
     * @param title the title to search for
     * @return a list of books with titles containing the given string
     */
    @QueryMapping
    public List<Book> booksByTitle(@Argument String title) {
        return bookService.getBooksByTitle(title);
    }

    /**
     * GraphQL query to get books by author (partial match, case insensitive)
     *
     * @param author the author to search for
     * @return a list of books with authors containing the given string
     */
    @QueryMapping
    public List<Book> booksByAuthor(@Argument String author) {
        return bookService.getBooksByAuthor(author);
    }

    /**
     * GraphQL query to get books by category
     *
     * @param categoryId the ID of the category to search for
     * @return a list of books in the given category
     */
    @QueryMapping
    public List<Book> booksByCategory(@Argument Long categoryId) {
        return categoryService.getCategoryById(categoryId)
                .map(bookService::getBooksByCategory)
                .orElse(List.of());
    }

    /**
     * GraphQL mutation to create a new book
     *
     * @param input the input containing book details
     * @return the created book
     */
    @MutationMapping
    public Book createBook(@Argument CreateBookInput input) {
        Category category = categoryService.getCategoryById(input.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + input.getCategoryId()));
        
        Book book = Book.builder()
                .title(input.getTitle())
                .author(input.getAuthor())
                .description(input.getDescription())
                .isbn(input.getIsbn())
                .price(BigDecimal.valueOf(input.getPrice()))
                .stockQuantity(input.getStockQuantity())
                .category(category)
                .publisher(input.getPublisher())
                .publicationYear(input.getPublicationYear())
                .language(input.getLanguage())
                .pageCount(input.getPageCount())
                .build();
        
        return bookService.createBook(book);
    }

    /**
     * GraphQL mutation to update an existing book
     *
     * @param input the input containing book details to update
     * @return the updated book if found, null otherwise
     */
    @MutationMapping
    public Book updateBook(@Argument UpdateBookInput input) {
        Book bookDetails = Book.builder().build();
        
        if (input.getTitle() != null) {
            bookDetails.setTitle(input.getTitle());
        }
        if (input.getAuthor() != null) {
            bookDetails.setAuthor(input.getAuthor());
        }
        if (input.getDescription() != null) {
            bookDetails.setDescription(input.getDescription());
        }
        if (input.getIsbn() != null) {
            bookDetails.setIsbn(input.getIsbn());
        }
        if (input.getPrice() != null) {
            bookDetails.setPrice(BigDecimal.valueOf(input.getPrice()));
        }
        if (input.getStockQuantity() != null) {
            bookDetails.setStockQuantity(input.getStockQuantity());
        }
        if (input.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(input.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + input.getCategoryId()));
            bookDetails.setCategory(category);
        }
        if (input.getPublisher() != null) {
            bookDetails.setPublisher(input.getPublisher());
        }
        if (input.getPublicationYear() != null) {
            bookDetails.setPublicationYear(input.getPublicationYear());
        }
        if (input.getLanguage() != null) {
            bookDetails.setLanguage(input.getLanguage());
        }
        if (input.getPageCount() != null) {
            bookDetails.setPageCount(input.getPageCount());
        }
        
        return bookService.updateBook(input.getId(), bookDetails).orElse(null);
    }

    /**
     * GraphQL mutation to delete a book
     *
     * @param id the ID of the book to delete
     * @return true if the book was deleted, false otherwise
     */
    @MutationMapping
    public boolean deleteBook(@Argument Long id) {
        return bookService.deleteBook(id);
    }

    /**
     * GraphQL mutation to update a book's stock quantity
     *
     * @param id the ID of the book to update
     * @param quantity the quantity to add (positive) or subtract (negative)
     * @return the updated book if found, null otherwise
     */
    @MutationMapping
    public Book updateBookStock(@Argument Long id, @Argument Integer quantity) {
        return bookService.updateBookStock(id, quantity).orElse(null);
    }

    /**
     * Input class for creating a book
     */
    public static class CreateBookInput {
        private String title;
        private String author;
        private String description;
        private String isbn;
        private Double price;
        private Integer stockQuantity;
        private Long categoryId;
        private String publisher;
        private Integer publicationYear;
        private String language;
        private Integer pageCount;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public Integer getStockQuantity() {
            return stockQuantity;
        }

        public void setStockQuantity(Integer stockQuantity) {
            this.stockQuantity = stockQuantity;
        }

        public Long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }

        public String getPublisher() {
            return publisher;
        }

        public void setPublisher(String publisher) {
            this.publisher = publisher;
        }

        public Integer getPublicationYear() {
            return publicationYear;
        }

        public void setPublicationYear(Integer publicationYear) {
            this.publicationYear = publicationYear;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public Integer getPageCount() {
            return pageCount;
        }

        public void setPageCount(Integer pageCount) {
            this.pageCount = pageCount;
        }
    }

    /**
     * Input class for updating a book
     */
    public static class UpdateBookInput {
        private Long id;
        private String title;
        private String author;
        private String description;
        private String isbn;
        private Double price;
        private Integer stockQuantity;
        private Long categoryId;
        private String publisher;
        private Integer publicationYear;
        private String language;
        private Integer pageCount;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public Integer getStockQuantity() {
            return stockQuantity;
        }

        public void setStockQuantity(Integer stockQuantity) {
            this.stockQuantity = stockQuantity;
        }

        public Long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }

        public String getPublisher() {
            return publisher;
        }

        public void setPublisher(String publisher) {
            this.publisher = publisher;
        }

        public Integer getPublicationYear() {
            return publicationYear;
        }

        public void setPublicationYear(Integer publicationYear) {
            this.publicationYear = publicationYear;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public Integer getPageCount() {
            return pageCount;
        }

        public void setPageCount(Integer pageCount) {
            this.pageCount = pageCount;
        }
    }
}
