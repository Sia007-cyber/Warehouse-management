package ir.university.warehouse.model;

public class Transaction {

    private int transactionId;
    private int permissionId;
    private TransactionType type;
    private double amount;
    private String transactionDate;

    public Transaction() {}

    public Transaction(int transactionId, int permissionId, TransactionType type,
                        double amount, String transactionDate) {
        this.transactionId = transactionId;
        this.permissionId = permissionId;
        this.type = type;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", permissionId=" + permissionId +
                ", type=" + type +
                ", amount=" + amount +
                ", transactionDate='" + transactionDate + '\'' +
                '}';
    }
}