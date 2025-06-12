package com.example.graphqlbookstore.controller;

import com.example.graphqlbookstore.model.Category;
import com.example.graphqlbookstore.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * GraphQL query to get all categories
     *
     * @return a list of all categories
     */
    @QueryMapping
    public List<Category> categories() {
        return categoryService.getAllCategories();
    }

    /**
     * GraphQL query to get a category by ID
     *
     * @param id the ID of the category to find
     * @return the category if found, null otherwise
     */
    @QueryMapping
    public Category category(@Argument Long id) {
        return categoryService.getCategoryById(id).orElse(null);
    }

    /**
     * GraphQL query to get a category by name
     *
     * @param name the name of the category to find
     * @return the category if found, null otherwise
     */
    @QueryMapping
    public Category categoryByName(@Argument String name) {
        return categoryService.getCategoryByName(name).orElse(null);
    }

    /**
     * GraphQL mutation to create a new category
     *
     * @param input the input containing category details
     * @return the created category
     */
    @MutationMapping
    public Category createCategory(@Argument CreateCategoryInput input) {
        Category category = Category.builder()
                .name(input.getName())
                .description(input.getDescription())
                .build();
        
        return categoryService.createCategory(category);
    }

    /**
     * GraphQL mutation to update an existing category
     *
     * @param input the input containing category details to update
     * @return the updated category if found, null otherwise
     */
    @MutationMapping
    public Category updateCategory(@Argument UpdateCategoryInput input) {
        Category categoryDetails = Category.builder().build();
        
        if (input.getName() != null) {
            categoryDetails.setName(input.getName());
        }
        if (input.getDescription() != null) {
            categoryDetails.setDescription(input.getDescription());
        }
        
        return categoryService.updateCategory(input.getId(), categoryDetails).orElse(null);
    }

    /**
     * GraphQL mutation to delete a category
     *
     * @param id the ID of the category to delete
     * @return true if the category was deleted, false otherwise
     */
    @MutationMapping
    public boolean deleteCategory(@Argument Long id) {
        return categoryService.deleteCategory(id);
    }

    /**
     * Input class for creating a category
     */
    public static class CreateCategoryInput {
        private String name;
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    /**
     * Input class for updating a category
     */
    public static class UpdateCategoryInput {
        private Long id;
        private String name;
        private String description;

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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
