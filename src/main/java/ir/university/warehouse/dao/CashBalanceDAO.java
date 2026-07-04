package ir.university.warehouse.dao;

import ir.university.warehouse.model.CashBalance;
import java.sql.SQLException;

public interface CashBalanceDAO {
    CashBalance get() throws SQLException;
    void update(double newBalance) throws SQLException;
}