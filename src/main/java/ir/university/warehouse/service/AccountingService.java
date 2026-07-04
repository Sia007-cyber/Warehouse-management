package ir.university.warehouse.service;

import ir.university.warehouse.exception.InsufficientCashException;
import ir.university.warehouse.model.TransactionType;
import java.sql.SQLException;

public interface AccountingService {

    double getBalance() throws SQLException;

    /**
     * کسر مبلغ از موجودی نقدی (برای خرید). اگر موجودی کافی نبود، خطا می‌دهد.
     */
    void withdraw(double amount) throws SQLException, InsufficientCashException;

    /**
     * افزودن مبلغ به موجودی نقدی (برای فروش تکمیل‌شده).
     */
    void deposit(double amount) throws SQLException;

    /**
     * ثبت یک تراکنش مالی مرتبط با یک مجوز.
     */
    void recordTransaction(int permissionId, TransactionType type, double amount, String date) throws SQLException;
}