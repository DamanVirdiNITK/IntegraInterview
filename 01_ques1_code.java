import java.util.ArrayList;
import java.util.List;

class Order {
    private final int orderId;
    private final double amount;

    public Order(int orderId, double amount) {
        this.orderId = orderId;
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public int getOrderId() {
        return orderId;
    }

    @Override
    public String toString() {
        return "Order ID: " + orderId + ", Amount: " + amount + " Euros";
    }
}

// Abstract Validator for order conditions
abstract class OrderValidator {
    protected OrderValidator next;

    public void setNext(OrderValidator next) {
        this.next = next;
    }

    public void validate(Order order) {
        if (check(order)) {
            if (next != null) {
                next.validate(order);
            } else {
                process(order);
            }
        } else {
            System.out.println("Order " + order.getOrderId() + " rejected.");
        }
    }

    protected abstract boolean check(Order order);

    protected void process(Order order) {
        System.out.println("Order " + order + " has been processed.");
    }
}

// Minimum Amount Condition
class MinAmountValidator extends OrderValidator {
    private final double minAmount;

    public MinAmountValidator(double minAmount) {
        this.minAmount = minAmount;
    }

    @Override
    protected boolean check(Order order) {
        return order.getAmount() > minAmount;
    }
}

// Future additional condition (e.g., Max Amount Validator)
class MaxAmountValidator extends OrderValidator {
    private final double maxAmount;

    public MaxAmountValidator(double maxAmount) {
        this.maxAmount = maxAmount;
    }

    @Override
    protected boolean check(Order order) {
        return order.getAmount() < maxAmount;
    }
}

// Order Processor
class OrderProcessor {
    private final OrderValidator validatorChain;

    public OrderProcessor(OrderValidator validatorChain) {
        this.validatorChain = validatorChain;
    }

    public void processOrder(Order order) {
        validatorChain.validate(order);
    }
}

// Main Class
public class OrderManagement {
    public static void main(String[] args) {
        // Create validation chain
        OrderValidator minAmountValidator = new MinAmountValidator(1000.0);
        OrderValidator maxAmountValidator = new MaxAmountValidator(5000.0);
        
        minAmountValidator.setNext(maxAmountValidator); // Chain the validators

        OrderProcessor processor = new OrderProcessor(minAmountValidator);

        // Sample Orders
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(1, 1200.0));  // Should pass
        orders.add(new Order(2, 900.0));   // Should be rejected (min amount)
        orders.add(new Order(3, 7000.0));  // Should be rejected (max amount)

        for (Order order : orders) {
            processor.processOrder(order);
        }
    }
}
