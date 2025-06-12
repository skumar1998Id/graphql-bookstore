package com.example.graphqlbookstore.service;

import com.example.graphqlbookstore.model.Category;
import com.example.graphqlbookstore.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Get all categories
     *
     * @return a list of all categories
     */
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Get a category by ID
     *
     * @param id the ID of the category to find
     * @return an Optional containing the category if found
     */
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Get a category by name
     *
     * @param name the name of the category to find
     * @return an Optional containing the category if found
     */
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    /**
     * Get a category by name (partial match, case insensitive)
     *
     * @param name the name to search for
     * @return an Optional containing the category if found
     */
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryByNameContaining(String name) {
        return categoryRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Create a new category
     *
     * @param category the category to create
     * @return the created category
     * @throws IllegalArgumentException if a category with the same name already exists
     */
    @Transactional
    public Category createCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("A category with name " + category.getName() + " already exists");
        }
        return categoryRepository.save(category);
    }

    /**
     * Update an existing category
     *
     * @param id the ID of the category to update
     * @param categoryDetails the updated category details
     * @return an Optional containing the updated category if found
     * @throws IllegalArgumentException if updating the name to one that already exists
     */
    @Transactional
    public Optional<Category> updateCategory(Long id, Category categoryDetails) {
        return categoryRepository.findById(id).map(existingCategory -> {
            // Check if name is being changed and if the new name already exists
            if (categoryDetails.getName() != null && 
                !existingCategory.getName().equals(categoryDetails.getName()) && 
                categoryRepository.existsByName(categoryDetails.getName())) {
                throw new IllegalArgumentException("A category with name " + categoryDetails.getName() + " already exists");
            }
            
            // Update fields if they are not null
            if (categoryDetails.getName() != null) {
                existingCategory.setName(categoryDetails.getName());
            }
            if (categoryDetails.getDescription() != null) {
                existingCategory.setDescription(categoryDetails.getDescription());
            }
            
            return categoryRepository.save(existingCategory);
        });
    }

    /**
     * Delete a category by ID
     *
     * @param id the ID of the category to delete
     * @return true if the category was deleted, false if the category was not found
     */
    @Transactional
    public boolean deleteCategory(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
