package com.example.graphqlbookstore.repository;

import com.example.graphqlbookstore.model.Order;
import com.example.graphqlbookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Find orders by user
     * 
     * @param user the user to search for
     * @return a list of orders for the given user
     */
    List<Order> findByUser(User user);
    
    /**
     * Find orders by status
     * 
     * @param status the status to search for
     * @return a list of orders with the given status
     */
    List<Order> findByStatus(Order.OrderStatus status);
    
    /**
     * Find orders created after the given date
     * 
     * @param date the date to search after
     * @return a list of orders created after the given date
     */
    List<Order> findByCreatedAtAfter(ZonedDateTime date);
    
    /**
     * Find orders created before the given date
     * 
     * @param date the date to search before
     * @return a list of orders created before the given date
     */
    List<Order> findByCreatedAtBefore(ZonedDateTime date);
    
    /**
     * Find orders by user and status
     * 
     * @param user the user to search for
     * @param status the status to search for
     * @return a list of orders for the given user with the given status
     */
    List<Order> findByUserAndStatus(User user, Order.OrderStatus status);
}
