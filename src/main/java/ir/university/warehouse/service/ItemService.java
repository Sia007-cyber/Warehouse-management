package ir.university.warehouse.service;

import ir.university.warehouse.exception.EntityNotFoundException;
import ir.university.warehouse.exception.ValidationException;
import ir.university.warehouse.model.Item;
import java.sql.SQLException;
import java.util.List;

public interface ItemService {

    Item createItem(String itemCode, String name, String description, int categoryId)
            throws SQLException, ValidationException, EntityNotFoundException;

    Item getItemById(int itemId) throws SQLException, EntityNotFoundException;

    List<Item> getAllItems() throws SQLException;

    Item updateItem(int itemId, String itemCode, String name, String description, int categoryId)
            throws SQLException, ValidationException, EntityNotFoundException;

    void deleteItem(int itemId) throws SQLException, EntityNotFoundException;
}