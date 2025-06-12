package com.example.graphqlbookstore.repository;

import com.example.graphqlbookstore.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Find a category by name
     * 
     * @param name the name to search for
     * @return an Optional containing the category if found
     */
    Optional<Category> findByName(String name);
    
    /**
     * Find a category by name containing the given string (case insensitive)
     * 
     * @param name the name to search for
     * @return an Optional containing the category if found
     */
    Optional<Category> findByNameContainingIgnoreCase(String name);
    
    /**
     * Check if a category exists with the given name
     * 
     * @param name the name to check
     * @return true if a category exists with the name, false otherwise
     */
    boolean existsByName(String name);
}
