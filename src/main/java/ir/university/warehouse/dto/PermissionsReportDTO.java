package ir.university.warehouse.dto;

import ir.university.warehouse.model.Permission;
import java.util.List;

public class PermissionsReportDTO {

    private final List<Permission> pending;
    private final List<Permission> completed;

    public PermissionsReportDTO(List<Permission> pending, List<Permission> completed) {
        this.pending = pending;
        this.completed = completed;
    }

    public List<Permission> getPending() { return pending; }
    public List<Permission> getCompleted() { return completed; }

    @Override
    public String toString() {
        return "مجوزهای در انتظار: " + pending.size() + " | مجوزهای انجام‌شده: " + completed.size();
    }
}