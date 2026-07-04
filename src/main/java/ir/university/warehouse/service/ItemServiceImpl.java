package ir.university.warehouse.service;

import ir.university.warehouse.dao.CategoryDAO;
import ir.university.warehouse.dao.ItemDAO;
import ir.university.warehouse.exception.EntityNotFoundException;
import ir.university.warehouse.exception.ValidationException;
import ir.university.warehouse.model.Item;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ItemServiceImpl implements ItemService {

    private final ItemDAO itemDAO;
    private final CategoryDAO categoryDAO;

    public ItemServiceImpl(ItemDAO itemDAO, CategoryDAO categoryDAO) {
        this.itemDAO = itemDAO;
        this.categoryDAO = categoryDAO;
    }

    @Override
    public Item createItem(String itemCode, String name, String description, int categoryId)
            throws SQLException, ValidationException, EntityNotFoundException {
        validateBasics(itemCode, name);
        ensureCategoryExists(categoryId);
        ensureCodeNotDuplicate(itemCode, null);

        Item item = new Item();
        item.setItemCode(itemCode.trim());
        item.setName(name.trim());
        item.setDescription(description);
        item.setCategoryId(categoryId);
        return itemDAO.insert(item);
    }

    @Override
    public Item getItemById(int itemId) throws SQLException, EntityNotFoundException {
        return itemDAO.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("کالایی با شناسه " + itemId + " یافت نشد."));
    }

    @Override
    public List<Item> getAllItems() throws SQLException {
        return itemDAO.findAll();
    }

    @Override
    public Item updateItem(int itemId, String itemCode, String name, String description, int categoryId)
            throws SQLException, ValidationException, EntityNotFoundException {
        validateBasics(itemCode, name);
        ensureCategoryExists(categoryId);
        ensureCodeNotDuplicate(itemCode, itemId);

        Item existing = getItemById(itemId);
        existing.setItemCode(itemCode.trim());
        existing.setName(name.trim());
        existing.setDescription(description);
        existing.setCategoryId(categoryId);
        itemDAO.update(existing);
        return existing;
    }

    @Override
    public void deleteItem(int itemId) throws SQLException, EntityNotFoundException {
        getItemById(itemId);
        itemDAO.delete(itemId);
    }

    private void validateBasics(String itemCode, String name) throws ValidationException {
        if (itemCode == null || itemCode.isBlank()) {
            throw new ValidationException("کد کالا نمی‌تواند خالی باشد.");
        }
        if (name == null || name.isBlank()) {
            throw new ValidationException("نام کالا نمی‌تواند خالی باشد.");
        }
    }

    private void ensureCategoryExists(int categoryId) throws SQLException, EntityNotFoundException {
        if (categoryDAO.findById(categoryId).isEmpty()) {
            throw new EntityNotFoundException("دسته‌بندی با شناسه " + categoryId + " یافت نشد.");
        }
    }

    private void ensureCodeNotDuplicate(String itemCode, Integer excludeItemId) throws SQLException, ValidationException {
        Optional<Item> existing = itemDAO.findByCode(itemCode.trim());
        if (existing.isPresent() && (excludeItemId == null || existing.get().getItemId() != excludeItemId)) {
            throw new ValidationException("کالایی با کد «" + itemCode + "» از قبل وجود دارد.");
        }
    }
}