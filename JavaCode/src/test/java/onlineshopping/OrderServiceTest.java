package onlineshopping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest {

    private OrderService orderService;
    private PaymentService paymentService;
    private InventoryService inventoryService;
    private ShoppingCart shoppingCart;

    @BeforeEach
    public void setUp() {
        paymentService = new PaymentService();
        inventoryService = new InventoryService();
        orderService = new OrderService(paymentService, inventoryService);
        shoppingCart = new ShoppingCart(new Customer("Alice", CustomerType.REGULAR), new DiscountService());
    }

    @Test
    public void testPlaceOrderSuccessful() {
        // Arrange: Add items to the shopping cart
        Product product = new Product("Cup", 400.0, 10);
        shoppingCart.addItem(new CartItem(product, 2));

        // Act: Place an order
        boolean isOrderPlaced = orderService.placeOrder(shoppingCart, "1234567890123456");

        // Assert: Order should be placed successfully
        assertTrue(isOrderPlaced, "Order should be placed successfully with valid payment and inventory.");
    }

    @Test
    public void testPlaceOrderWithInsufficientStock() {
        // Arrange: Add items exceeding available stock to the shopping cart
        Product product = new Product("Cup", 400.0, 1); // Only 1 item in stock
        shoppingCart.addItem(new CartItem(product, 2)); // Attempt to order 2 items

        // Act: Place an order
        boolean isOrderPlaced = orderService.placeOrder(shoppingCart, "1234567890123456");

        // Assert: Order should not be placed due to insufficient stock
        assertFalse(isOrderPlaced, "Order should not be placed due to insufficient stock.");
    }

    @Test
    public void testInventoryUpdate() {
        // Arrange: Add items to the shopping cart
        Product product = new Product("Cup", 400.0, 10);
        CartItem cartItem = new CartItem(product, 3);

        // Act: Update inventory
        inventoryService.updateStock(cartItem);

        // Assert: Stock should be reduced by the quantity ordered
        assertEquals(7, product.getStock(), "Inventory should be updated correctly after placing an order.");
    }
}
