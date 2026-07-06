package ir.university.warehouse.model;

public class Category {

    private int categoryId;
    private String name;
    private Integer parentId; // nullable - null یعنی دسته‌بندی ریشه (بدون والد)

    public Category() {}

    public Category(int categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    public Category(int categoryId, String name, Integer parentId) {
        this.categoryId = categoryId;
        this.name = name;
        this.parentId = parentId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                '}';
    }
}