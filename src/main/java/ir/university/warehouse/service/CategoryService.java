package ir.university.warehouse.service;

import ir.university.warehouse.exception.EntityNotFoundException;
import ir.university.warehouse.exception.ValidationException;
import ir.university.warehouse.model.Category;
import java.sql.SQLException;
import java.util.List;

public interface CategoryService {

    Category createCategory(String name) throws SQLException, ValidationException;

    Category getCategoryById(int categoryId) throws SQLException, EntityNotFoundException;

    List<Category> getAllCategories() throws SQLException;

    Category updateCategory(int categoryId, String name)
            throws SQLException, ValidationException, EntityNotFoundException;

    void deleteCategory(int categoryId) throws SQLException, EntityNotFoundException;
}