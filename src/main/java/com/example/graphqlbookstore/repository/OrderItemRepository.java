package com.example.graphqlbookstore.repository;

import com.example.graphqlbookstore.model.Book;
import com.example.graphqlbookstore.model.Order;
import com.example.graphqlbookstore.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    /**
     * Find order items by order
     * 
     * @param order the order to search for
     * @return a list of order items for the given order
     */
    List<OrderItem> findByOrder(Order order);
    
    /**
     * Find order items by book
     * 
     * @param book the book to search for
     * @return a list of order items for the given book
     */
    List<OrderItem> findByBook(Book book);
    
    /**
     * Find order items by order and book
     * 
     * @param order the order to search for
     * @param book the book to search for
     * @return a list of order items for the given order and book
     */
    List<OrderItem> findByOrderAndBook(Order order, Book book);
    
    /**
     * Delete order items by order
     * 
     * @param order the order to delete items for
     */
    void deleteByOrder(Order order);
}
