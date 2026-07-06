package ir.university.warehouse.service;

import ir.university.warehouse.dao.CategoryDAO;
import ir.university.warehouse.exception.EntityNotFoundException;
import ir.university.warehouse.exception.ValidationException;
import ir.university.warehouse.model.Category;

import java.sql.SQLException;
import java.util.List;

public class CategoryServiceImpl implements CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryServiceImpl(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    @Override
    public Category createCategory(String name) throws SQLException, ValidationException {
        try {
            return createCategory(name, null);
        } catch (EntityNotFoundException e) {
            // never happens with parentId == null
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Category createCategory(String name, Integer parentId)
            throws SQLException, ValidationException, EntityNotFoundException {
        validateName(name);
        checkNameNotDuplicate(name, null);
        if (parentId != null) {
            getCategoryById(parentId); // اطمینان از وجود دسته‌ی والد
        }

        Category category = new Category();
        category.setName(name.trim());
        category.setParentId(parentId);
        return categoryDAO.insert(category);
    }

    @Override
    public Category getCategoryById(int categoryId) throws SQLException, EntityNotFoundException {
        return categoryDAO.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("دسته‌بندی با شناسه " + categoryId + " یافت نشد."));
    }

    @Override
    public List<Category> getAllCategories() throws SQLException {
        return categoryDAO.findAll();
    }

    @Override
    public Category updateCategory(int categoryId, String name)
            throws SQLException, ValidationException, EntityNotFoundException {
        Category existing = getCategoryById(categoryId);
        return updateCategory(categoryId, name, existing.getParentId());
    }

    @Override
    public Category updateCategory(int categoryId, String name, Integer parentId)
            throws SQLException, ValidationException, EntityNotFoundException {
        validateName(name);
        checkNameNotDuplicate(name, categoryId);
        if (parentId != null) {
            if (parentId == categoryId) {
                throw new ValidationException("یک دسته‌بندی نمی‌تواند والد خودش باشد.");
            }
            getCategoryById(parentId); // اطمینان از وجود دسته‌ی والد
        }

        Category existing = getCategoryById(categoryId);
        existing.setName(name.trim());
        existing.setParentId(parentId);
        categoryDAO.update(existing);
        return existing;
    }

    @Override
    public void deleteCategory(int categoryId) throws SQLException, EntityNotFoundException {
        getCategoryById(categoryId);
        categoryDAO.delete(categoryId);
    }

    private void validateName(String name) throws ValidationException {
        if (name == null || name.isBlank()) {
            throw new ValidationException("نام دسته‌بندی نمی‌تواند خالی باشد.");
        }
    }

    private void checkNameNotDuplicate(String name, Integer excludeCategoryId) throws SQLException, ValidationException {
        for (Category c : categoryDAO.findAll()) {
            boolean sameName = c.getName().equalsIgnoreCase(name.trim());
            boolean isSelf = excludeCategoryId != null && c.getCategoryId() == excludeCategoryId;
            if (sameName && !isSelf) {
                throw new ValidationException("دسته‌بندی با نام «" + name + "» از قبل وجود دارد.");
            }
        }
    }
}