package onlineshopping;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OrderService {
    private PaymentService paymentService;
    private InventoryService inventoryService;
    private Lock orderLock = new ReentrantLock();

    public OrderService(PaymentService paymentService, InventoryService inventoryService) {
        this.paymentService = paymentService;
        this.inventoryService = inventoryService;
    }

    public boolean placeOrder(ShoppingCart cart, String creditCardNumber) {
        orderLock.lock(); // Ensure only one thread places an order at a time
        try {
            // Check stock and update it
            for (CartItem item : cart.getItems()) {
                inventoryService.updateStock(item);
            }

            // Apply discounts and process payment
            double total = cart.calculateTotal();
            return paymentService.processPayment(creditCardNumber, total);
        } catch (Exception e) {
            System.err.println("Order failed: " + e.getMessage());
            return false;
        } finally {
            orderLock.unlock();
        }
    }
}


