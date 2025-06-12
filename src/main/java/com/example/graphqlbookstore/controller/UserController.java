package com.example.graphqlbookstore.controller;

import com.example.graphqlbookstore.model.Order;
import com.example.graphqlbookstore.model.User;
import com.example.graphqlbookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GraphQL query to get all users
     *
     * @return a list of all users
     */
    @QueryMapping
    public List<User> users() {
        return userService.getAllUsers();
    }

    /**
     * GraphQL query to get a user by ID
     *
     * @param id the ID of the user to find
     * @return the user if found, null otherwise
     */
    @QueryMapping
    public User user(@Argument Long id) {
        return userService.getUserById(id).orElse(null);
    }

    /**
     * GraphQL query to get a user by email
     *
     * @param email the email of the user to find
     * @return the user if found, null otherwise
     */
    @QueryMapping
    public User userByEmail(@Argument String email) {
        return userService.getUserByEmail(email).orElse(null);
    }

    /**
     * GraphQL query to get orders for a user
     *
     * @param userId the ID of the user to find orders for
     * @return a list of orders for the user
     */
    @QueryMapping
    public List<Order> userOrders(@Argument Long userId) {
        return userService.getUserOrders(userId);
    }

    /**
     * GraphQL mutation to create a new user
     *
     * @param input the input containing user details
     * @return the created user
     */
    @MutationMapping
    public User createUser(@Argument CreateUserInput input) {
        User user = User.builder()
                .name(input.getName())
                .email(input.getEmail())
                .password(input.getPassword())
                .address(input.getAddress())
                .phoneNumber(input.getPhoneNumber())
                .build();
        
        return userService.createUser(user);
    }

    /**
     * GraphQL mutation to update an existing user
     *
     * @param input the input containing user details to update
     * @return the updated user if found, null otherwise
     */
    @MutationMapping
    public User updateUser(@Argument UpdateUserInput input) {
        User userDetails = User.builder().build();
        
        if (input.getName() != null) {
            userDetails.setName(input.getName());
        }
        if (input.getEmail() != null) {
            userDetails.setEmail(input.getEmail());
        }
        if (input.getPassword() != null) {
            userDetails.setPassword(input.getPassword());
        }
        if (input.getAddress() != null) {
            userDetails.setAddress(input.getAddress());
        }
        if (input.getPhoneNumber() != null) {
            userDetails.setPhoneNumber(input.getPhoneNumber());
        }
        
        return userService.updateUser(input.getId(), userDetails).orElse(null);
    }

    /**
     * GraphQL mutation to delete a user
     *
     * @param id the ID of the user to delete
     * @return true if the user was deleted, false otherwise
     */
    @MutationMapping
    public boolean deleteUser(@Argument Long id) {
        return userService.deleteUser(id);
    }

    /**
     * GraphQL mutation to authenticate a user
     *
     * @param email the user's email
     * @param password the user's password
     * @return the authenticated user if credentials are valid, null otherwise
     */
    @MutationMapping
    public User authenticateUser(@Argument String email, @Argument String password) {
        return userService.authenticateUser(email, password).orElse(null);
    }

    /**
     * Input class for creating a user
     */
    public static class CreateUserInput {
        private String name;
        private String email;
        private String password;
        private String address;
        private String phoneNumber;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }

    /**
     * Input class for updating a user
     */
    public static class UpdateUserInput {
        private Long id;
        private String name;
        private String email;
        private String password;
        private String address;
        private String phoneNumber;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
}
