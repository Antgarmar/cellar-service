package tu.somelier.cellar.domain.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(int requested, int available) {
        super("Insufficient stock: requested " + requested + " but only " + available + " available");
    }
}
