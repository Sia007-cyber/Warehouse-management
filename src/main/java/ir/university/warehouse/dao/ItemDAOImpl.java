package ir.university.warehouse.dao;

import ir.university.warehouse.db.DatabaseConnection;
import ir.university.warehouse.model.Item;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemDAOImpl implements ItemDAO {

    @Override
    public Item insert(Item item) throws SQLException {
        String sql = "INSERT INTO Items (item_code, name, description, category_id) VALUES (?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, item.getItemCode());
            stmt.setString(2, item.getName());
            stmt.setString(3, item.getDescription());
            stmt.setInt(4, item.getCategoryId());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    item.setItemId(keys.getInt(1));
                }
            }
        }
        return item;
    }

    @Override
    public Optional<Item> findById(int itemId) throws SQLException {
        String sql = "SELECT * FROM Items WHERE item_id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Item> findByCode(String itemCode) throws SQLException {
        String sql = "SELECT * FROM Items WHERE item_code = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Item> findAll() throws SQLException {
        String sql = "SELECT * FROM Items";
        Connection conn = DatabaseConnection.getConnection();
        List<Item> items = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(mapRow(rs));
            }
        }
        return items;
    }

    @Override
    public void update(Item item) throws SQLException {
        String sql = "UPDATE Items SET item_code = ?, name = ?, description = ?, category_id = ? WHERE item_id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getItemCode());
            stmt.setString(2, item.getName());
            stmt.setString(3, item.getDescription());
            stmt.setInt(4, item.getCategoryId());
            stmt.setInt(5, item.getItemId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int itemId) throws SQLException {
        String sql = "DELETE FROM Items WHERE item_id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            stmt.executeUpdate();
        }
    }

    private Item mapRow(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setItemId(rs.getInt("item_id"));
        item.setItemCode(rs.getString("item_code"));
        item.setName(rs.getString("name"));
        item.setDescription(rs.getString("description"));
        item.setCategoryId(rs.getInt("category_id"));
        return item;
    }
}