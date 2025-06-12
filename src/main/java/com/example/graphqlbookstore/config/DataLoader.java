package com.example.graphqlbookstore.config;

import com.example.graphqlbookstore.model.*;
import com.example.graphqlbookstore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public DataLoader(UserRepository userRepository, CategoryRepository categoryRepository,
                     BookRepository bookRepository, OrderRepository orderRepository,
                     OrderItemRepository orderItemRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public void run(String... args) {
        // Check if data already exists
        if (userRepository.count() == 0) {
            loadUsers();
            loadCategories();
            loadBooks();
            loadOrders();
        }
    }

    private void loadUsers() {
        List<User> users = Arrays.asList(
            User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .address("123 Main St, Anytown, USA")
                .phoneNumber("555-123-4567")
                .build(),
            User.builder()
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .password("password456")
                .address("456 Oak Ave, Somewhere, USA")
                .phoneNumber("555-987-6543")
                .build(),
            User.builder()
                .name("Bob Johnson")
                .email("bob.johnson@example.com")
                .password("password789")
                .address("789 Pine Rd, Nowhere, USA")
                .phoneNumber("555-456-7890")
                .build()
        );
        
        userRepository.saveAll(users);
        
        System.out.println("Sample users loaded into database");
    }
    
    private void loadCategories() {
        List<Category> categories = Arrays.asList(
            Category.builder()
                .name("Fiction")
                .description("Novels, short stories, and other fictional works")
                .build(),
            Category.builder()
                .name("Non-Fiction")
                .description("Biographies, histories, and other factual works")
                .build(),
            Category.builder()
                .name("Science Fiction")
                .description("Futuristic and speculative fiction")
                .build(),
            Category.builder()
                .name("Mystery")
                .description("Detective stories and thrillers")
                .build(),
            Category.builder()
                .name("Romance")
                .description("Love stories and romantic fiction")
                .build()
        );
        
        categoryRepository.saveAll(categories);
        
        System.out.println("Sample categories loaded into database");
    }
    
    private void loadBooks() {
        Category fiction = categoryRepository.findByName("Fiction").orElseThrow();
        Category nonFiction = categoryRepository.findByName("Non-Fiction").orElseThrow();
        Category sciFi = categoryRepository.findByName("Science Fiction").orElseThrow();
        Category mystery = categoryRepository.findByName("Mystery").orElseThrow();
        Category romance = categoryRepository.findByName("Romance").orElseThrow();
        
        List<Book> books = Arrays.asList(
            Book.builder()
                .title("The Great Novel")
                .author("Jane Author")
                .description("A sweeping epic of love and loss")
                .isbn("978-1234567890")
                .price(new BigDecimal("19.99"))
                .stockQuantity(50)
                .category(fiction)
                .publisher("Big Publishing House")
                .publicationYear(2020)
                .language("English")
                .pageCount(320)
                .build(),
            Book.builder()
                .title("History of Everything")
                .author("John Historian")
                .description("A comprehensive history of the world")
                .isbn("978-0987654321")
                .price(new BigDecimal("24.99"))
                .stockQuantity(30)
                .category(nonFiction)
                .publisher("Academic Press")
                .publicationYear(2019)
                .language("English")
                .pageCount(500)
                .build(),
            Book.builder()
                .title("Space Adventures")
                .author("Zoe Spacer")
                .description("Thrilling adventures in deep space")
                .isbn("978-5678901234")
                .price(new BigDecimal("15.99"))
                .stockQuantity(40)
                .category(sciFi)
                .publisher("Future Books")
                .publicationYear(2021)
                .language("English")
                .pageCount(280)
                .build(),
            Book.builder()
                .title("The Mystery of the Missing Book")
                .author("Sherlock Writer")
                .description("A detective solves the case of a missing rare book")
                .isbn("978-8765432109")
                .price(new BigDecimal("12.99"))
                .stockQuantity(25)
                .category(mystery)
                .publisher("Mystery House")
                .publicationYear(2022)
                .language("English")
                .pageCount(240)
                .build(),
            Book.builder()
                .title("Love in Paris")
                .author("Amour Writer")
                .description("A romantic story set in the city of love")
                .isbn("978-2345678901")
                .price(new BigDecimal("14.99"))
                .stockQuantity(35)
                .category(romance)
                .publisher("Heart Press")
                .publicationYear(2020)
                .language("English")
                .pageCount(260)
                .build()
        );
        
        bookRepository.saveAll(books);
        
        System.out.println("Sample books loaded into database");
    }
    
    private void loadOrders() {
        User john = userRepository.findByEmail("john.doe@example.com").orElseThrow();
        User jane = userRepository.findByEmail("jane.smith@example.com").orElseThrow();
        
        Book book1 = bookRepository.findByIsbn("978-1234567890").orElseThrow();
        Book book2 = bookRepository.findByIsbn("978-0987654321").orElseThrow();
        Book book3 = bookRepository.findByIsbn("978-5678901234").orElseThrow();
        
        // Create order for John
        Order johnOrder = Order.builder()
                .user(john)
                .status(Order.OrderStatus.DELIVERED)
                .totalAmount(new BigDecimal("44.98"))
                .shippingAddress(john.getAddress())
                .billingAddress(john.getAddress())
                .paymentMethod("Credit Card")
                .trackingNumber("TRK123456789")
                .build();
        
        OrderItem johnItem1 = OrderItem.builder()
                .book(book1)
                .quantity(1)
                .price(book1.getPrice())
                .build();
        
        OrderItem johnItem2 = OrderItem.builder()
                .book(book2)
                .quantity(1)
                .price(book2.getPrice())
                .build();
        
        johnOrder.addOrderItem(johnItem1);
        johnOrder.addOrderItem(johnItem2);
        
        orderRepository.save(johnOrder);
        
        // Create order for Jane
        Order janeOrder = Order.builder()
                .user(jane)
                .status(Order.OrderStatus.PROCESSING)
                .totalAmount(new BigDecimal("15.99"))
                .shippingAddress(jane.getAddress())
                .billingAddress(jane.getAddress())
                .paymentMethod("PayPal")
                .build();
        
        OrderItem janeItem1 = OrderItem.builder()
                .book(book3)
                .quantity(1)
                .price(book3.getPrice())
                .build();
        
        janeOrder.addOrderItem(janeItem1);
        
        orderRepository.save(janeOrder);
        
        System.out.println("Sample orders loaded into database");
    }
}
