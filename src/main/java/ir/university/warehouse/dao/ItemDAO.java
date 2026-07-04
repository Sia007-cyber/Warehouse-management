package ir.university.warehouse.dao;

import ir.university.warehouse.model.Item;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ItemDAO {
    Item insert(Item item) throws SQLException;
    Optional<Item> findById(int itemId) throws SQLException;
    Optional<Item> findByCode(String itemCode) throws SQLException;
    List<Item> findAll() throws SQLException;
    void update(Item item) throws SQLException;
    void delete(int itemId) throws SQLException;
}