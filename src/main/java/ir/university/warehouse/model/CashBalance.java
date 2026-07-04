package ir.university.warehouse.model;

public class CashBalance {

    private int id;
    private double balance;

    public CashBalance() {}

    public CashBalance(int id, double balance) {
        this.id = id;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "CashBalance{" +
                "id=" + id +
                ", balance=" + balance +
                '}';
    }
}