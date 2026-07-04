package ir.university.warehouse.service;

import ir.university.warehouse.dao.CashBalanceDAO;
import ir.university.warehouse.dao.TransactionDAO;
import ir.university.warehouse.exception.InsufficientCashException;
import ir.university.warehouse.model.Transaction;
import ir.university.warehouse.model.TransactionType;

import java.sql.SQLException;

public class AccountingServiceImpl implements AccountingService {

    private final CashBalanceDAO cashBalanceDAO;
    private final TransactionDAO transactionDAO;

    public AccountingServiceImpl(CashBalanceDAO cashBalanceDAO, TransactionDAO transactionDAO) {
        this.cashBalanceDAO = cashBalanceDAO;
        this.transactionDAO = transactionDAO;
    }

    @Override
    public double getBalance() throws SQLException {
        return cashBalanceDAO.get().getBalance();
    }

    @Override
    public void withdraw(double amount) throws SQLException, InsufficientCashException {
        double currentBalance = getBalance();
        if (currentBalance < amount) {
            throw new InsufficientCashException(
                    "Insufficient cash balance. Current balance: " + currentBalance + ", required amount: " + amount);
        }
        cashBalanceDAO.update(currentBalance - amount);
    }

    @Override
    public void deposit(double amount) throws SQLException {
        double currentBalance = getBalance();
        cashBalanceDAO.update(currentBalance + amount);
    }

    @Override
    public void recordTransaction(int permissionId, TransactionType type, double amount, String date) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setPermissionId(permissionId);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setTransactionDate(date);
        transactionDAO.insert(transaction);
    }
}