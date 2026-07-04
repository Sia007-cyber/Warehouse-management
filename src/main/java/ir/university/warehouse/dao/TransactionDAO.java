package ir.university.warehouse.dao;

import ir.university.warehouse.model.Transaction;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TransactionDAO {

    Transaction insert(Transaction transaction) throws SQLException;

    Optional<Transaction> findById(int transactionId) throws SQLException;

    List<Transaction> findAll() throws SQLException;

    List<Transaction> findByDateRange(String startDate, String endDate) throws SQLException;
}