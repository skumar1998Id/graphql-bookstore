package com.example.graphqlbookstore.service;

import com.example.graphqlbookstore.model.Order;
import com.example.graphqlbookstore.model.User;
import com.example.graphqlbookstore.repository.OrderRepository;
import com.example.graphqlbookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public UserService(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Get all users
     *
     * @return a list of all users
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get a user by ID
     *
     * @param id the ID of the user to find
     * @return an Optional containing the user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Get a user by email
     *
     * @param email the email of the user to find
     * @return an Optional containing the user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Get orders for a user
     *
     * @param userId the ID of the user to find orders for
     * @return a list of orders for the user
     */
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(Long userId) {
        return userRepository.findById(userId)
                .map(orderRepository::findByUser)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
    }

    /**
     * Create a new user
     *
     * @param user the user to create
     * @return the created user
     * @throws IllegalArgumentException if a user with the same email already exists
     */
    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("A user with email " + user.getEmail() + " already exists");
        }
        return userRepository.save(user);
    }

    /**
     * Update an existing user
     *
     * @param id the ID of the user to update
     * @param userDetails the updated user details
     * @return an Optional containing the updated user if found
     * @throws IllegalArgumentException if updating the email to one that already exists
     */
    @Transactional
    public Optional<User> updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(existingUser -> {
            // Check if email is being changed and if the new email already exists
            if (userDetails.getEmail() != null && 
                !existingUser.getEmail().equals(userDetails.getEmail()) && 
                userRepository.existsByEmail(userDetails.getEmail())) {
                throw new IllegalArgumentException("A user with email " + userDetails.getEmail() + " already exists");
            }
            
            // Update fields if they are not null
            if (userDetails.getName() != null) {
                existingUser.setName(userDetails.getName());
            }
            if (userDetails.getEmail() != null) {
                existingUser.setEmail(userDetails.getEmail());
            }
            if (userDetails.getPassword() != null) {
                existingUser.setPassword(userDetails.getPassword());
            }
            if (userDetails.getAddress() != null) {
                existingUser.setAddress(userDetails.getAddress());
            }
            if (userDetails.getPhoneNumber() != null) {
                existingUser.setPhoneNumber(userDetails.getPhoneNumber());
            }
            
            return userRepository.save(existingUser);
        });
    }

    /**
     * Delete a user by ID
     *
     * @param id the ID of the user to delete
     * @return true if the user was deleted, false if the user was not found
     */
    @Transactional
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * Authenticate a user
     *
     * @param email the user's email
     * @param password the user's password
     * @return an Optional containing the authenticated user if credentials are valid
     */
    @Transactional(readOnly = true)
    public Optional<User> authenticateUser(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> user.getPassword().equals(password));
    }
}
