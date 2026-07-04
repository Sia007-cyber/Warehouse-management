package ir.university.warehouse.exception;

public class InsufficientStockException extends WarehouseException {

    public InsufficientStockException(String message) {
        super(message);
    }
}