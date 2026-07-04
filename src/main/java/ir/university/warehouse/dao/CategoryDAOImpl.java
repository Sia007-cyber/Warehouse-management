package ir.university.warehouse.dao;

import ir.university.warehouse.db.DatabaseConnection;
import ir.university.warehouse.model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryDAOImpl implements CategoryDAO {

    @Override
    public Category insert(Category category) throws SQLException {
        String sql = "INSERT INTO Categories (name) VALUES (?)";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, category.getName());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    category.setCategoryId(keys.getInt(1));
                }
            }
        }
        return category;
    }

    @Override
    public Optional<Category> findById(int categoryId) throws SQLException {
        String sql = "SELECT * FROM Categories WHERE category_id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Category> findAll() throws SQLException {
        String sql = "SELECT * FROM Categories";
        Connection conn = DatabaseConnection.getConnection();
        List<Category> categories = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categories.add(mapRow(rs));
            }
        }
        return categories;
    }

    @Override
    public void update(Category category) throws SQLException {
        String sql = "UPDATE Categories SET name = ? WHERE category_id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getName());
            stmt.setInt(2, category.getCategoryId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int categoryId) throws SQLException {
        String sql = "DELETE FROM Categories WHERE category_id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            stmt.executeUpdate();
        }
    }

    private Category mapRow(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setName(rs.getString("name"));
        return category;
    }
}