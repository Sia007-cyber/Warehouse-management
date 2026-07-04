package ir.university.warehouse.dao;

import ir.university.warehouse.model.Category;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CategoryDAO {
    Category insert(Category category) throws SQLException;
    Optional<Category> findById(int categoryId) throws SQLException;
    List<Category> findAll() throws SQLException;
    void update(Category category) throws SQLException;
    void delete(int categoryId) throws SQLException;
}