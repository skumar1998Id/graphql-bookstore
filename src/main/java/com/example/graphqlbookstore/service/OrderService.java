package com.example.graphqlbookstore.service;

import com.example.graphqlbookstore.model.Book;
import com.example.graphqlbookstore.model.Order;
import com.example.graphqlbookstore.model.OrderItem;
import com.example.graphqlbookstore.model.User;
import com.example.graphqlbookstore.repository.BookRepository;
import com.example.graphqlbookstore.repository.OrderItemRepository;
import com.example.graphqlbookstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, 
                        BookRepository bookRepository, BookService bookService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
    }

    /**
     * Get all orders
     *
     * @return a list of all orders
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Get an order by ID
     *
     * @param id the ID of the order to find
     * @return an Optional containing the order if found
     */
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * Get orders by user
     *
     * @param user the user to search for
     * @return a list of orders for the given user
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    /**
     * Get orders by status
     *
     * @param status the status to search for
     * @return a list of orders with the given status
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Get orders by user and status
     *
     * @param user the user to search for
     * @param status the status to search for
     * @return a list of orders for the given user with the given status
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByUserAndStatus(User user, Order.OrderStatus status) {
        return orderRepository.findByUserAndStatus(user, status);
    }

    /**
     * Get orders created after the given date
     *
     * @param date the date to search after
     * @return a list of orders created after the given date
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersCreatedAfter(ZonedDateTime date) {
        return orderRepository.findByCreatedAtAfter(date);
    }

    /**
     * Get orders created before the given date
     *
     * @param date the date to search before
     * @return a list of orders created before the given date
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersCreatedBefore(ZonedDateTime date) {
        return orderRepository.findByCreatedAtBefore(date);
    }

    /**
     * Create a new order
     *
     * @param order the order to create
     * @return the created order
     */
    @Transactional
    public Order createOrder(Order order) {
        // Set initial status if not set
        if (order.getStatus() == null) {
            order.setStatus(Order.OrderStatus.PENDING);
        }
        
        // Calculate total amount
        order.calculateTotalAmount();
        
        // Save the order
        Order savedOrder = orderRepository.save(order);
        
        // Update book stock quantities
        for (OrderItem item : order.getOrderItems()) {
            Book book = item.getBook();
            bookService.updateBookStock(book.getId(), -item.getQuantity());
        }
        
        return savedOrder;
    }

    /**
     * Add an item to an order
     *
     * @param orderId the ID of the order to add the item to
     * @param bookId the ID of the book to add
     * @param quantity the quantity to add
     * @return an Optional containing the updated order if found
     * @throws IllegalArgumentException if the book is not found or if there is insufficient stock
     */
    @Transactional
    public Optional<Order> addOrderItem(Long orderId, Long bookId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isEmpty()) {
            throw new IllegalArgumentException("Book not found with ID: " + bookId);
        }
        
        Book book = bookOpt.get();
        if (book.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock for book: " + book.getTitle());
        }
        
        return orderRepository.findById(orderId).map(order -> {
            // Check if the book is already in the order
            Optional<OrderItem> existingItemOpt = order.getOrderItems().stream()
                    .filter(item -> item.getBook().getId().equals(bookId))
                    .findFirst();
            
            if (existingItemOpt.isPresent()) {
                // Update existing item
                OrderItem existingItem = existingItemOpt.get();
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                orderItemRepository.save(existingItem);
            } else {
                // Create new item
                OrderItem newItem = OrderItem.builder()
                        .book(book)
                        .quantity(quantity)
                        .price(book.getPrice())
                        .build();
                order.addOrderItem(newItem);
            }
            
            // Update book stock
            bookService.updateBookStock(bookId, -quantity);
            
            // Recalculate total amount
            order.calculateTotalAmount();
            
            return orderRepository.save(order);
        });
    }

    /**
     * Remove an item from an order
     *
     * @param orderId the ID of the order to remove the item from
     * @param orderItemId the ID of the order item to remove
     * @return an Optional containing the updated order if found
     * @throws IllegalArgumentException if the order item is not found
     */
    @Transactional
    public Optional<Order> removeOrderItem(Long orderId, Long orderItemId) {
        Optional<OrderItem> orderItemOpt = orderItemRepository.findById(orderItemId);
        if (orderItemOpt.isEmpty()) {
            throw new IllegalArgumentException("Order item not found with ID: " + orderItemId);
        }
        
        OrderItem orderItem = orderItemOpt.get();
        
        return orderRepository.findById(orderId).map(order -> {
            if (!order.getOrderItems().contains(orderItem)) {
                throw new IllegalArgumentException("Order item does not belong to this order");
            }
            
            // Return the book stock
            bookService.updateBookStock(orderItem.getBook().getId(), orderItem.getQuantity());
            
            // Remove the item
            order.removeOrderItem(orderItem);
            orderItemRepository.delete(orderItem);
            
            // Recalculate total amount
            order.calculateTotalAmount();
            
            return orderRepository.save(order);
        });
    }

    /**
     * Update an order's status
     *
     * @param id the ID of the order to update
     * @param status the new status
     * @return an Optional containing the updated order if found
     */
    @Transactional
    public Optional<Order> updateOrderStatus(Long id, Order.OrderStatus status) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus(status);
            return orderRepository.save(order);
        });
    }

    /**
     * Update an order's shipping information
     *
     * @param id the ID of the order to update
     * @param shippingAddress the new shipping address
     * @param trackingNumber the new tracking number
     * @return an Optional containing the updated order if found
     */
    @Transactional
    public Optional<Order> updateOrderShipping(Long id, String shippingAddress, String trackingNumber) {
        return orderRepository.findById(id).map(order -> {
            if (shippingAddress != null) {
                order.setShippingAddress(shippingAddress);
            }
            if (trackingNumber != null) {
                order.setTrackingNumber(trackingNumber);
            }
            return orderRepository.save(order);
        });
    }

    /**
     * Cancel an order
     *
     * @param id the ID of the order to cancel
     * @return an Optional containing the updated order if found
     * @throws IllegalArgumentException if the order cannot be cancelled
     */
    @Transactional
    public Optional<Order> cancelOrder(Long id) {
        return orderRepository.findById(id).map(order -> {
            if (order.getStatus() == Order.OrderStatus.SHIPPED || 
                order.getStatus() == Order.OrderStatus.DELIVERED) {
                throw new IllegalArgumentException("Cannot cancel an order that has been shipped or delivered");
            }
            
            // Return the book stock
            for (OrderItem item : order.getOrderItems()) {
                bookService.updateBookStock(item.getBook().getId(), item.getQuantity());
            }
            
            order.setStatus(Order.OrderStatus.CANCELLED);
            return orderRepository.save(order);
        });
    }

    /**
     * Delete an order by ID
     *
     * @param id the ID of the order to delete
     * @return true if the order was deleted, false if the order was not found
     */
    @Transactional
    public boolean deleteOrder(Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
