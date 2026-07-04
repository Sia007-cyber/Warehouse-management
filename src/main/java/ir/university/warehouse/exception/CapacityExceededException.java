package ir.university.warehouse.exception;

public class CapacityExceededException extends WarehouseException {

    public CapacityExceededException(String message) {
        super(message);
    }
}