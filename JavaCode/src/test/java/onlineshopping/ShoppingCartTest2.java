package onlineshopping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShoppingCartTest2 {

    private ShoppingCart shoppingCart;
    private DiscountService discountService;

    @BeforeEach
    public void setUp() {
        // Initialize test environment
        discountService = new DiscountService();
        shoppingCart = new ShoppingCart(new Customer("Alice", CustomerType.REGULAR), discountService);
    }

    /*Normal Value Testing*/
    @Test
    public void testCalculateTotalWithMultipleProducts() {
        // Shopping cart contains multiple products with different quantities
        Product laptop = new Product("Laptop", 1000.0, 5);
        Product mouse = new Product("Mouse", 100.0, 20);

        CartItem item1 = new CartItem(laptop, 1);
        CartItem item2 = new CartItem(mouse, 2);

        shoppingCart.addItem(item1);
        shoppingCart.addItem(item2);

        // Verify if the total price is 1100.0 (1000 + 2 * 100)
        assertEquals(1200.0, shoppingCart.calculateTotal(), "Total price should be the sum of all items' prices.");
    }

    @Test
    public void testCalculateTotalWithOneProduct() {
        // Shopping cart contains only one product
        Product product = new Product("Cup", 400.0, 10);
        CartItem item = new CartItem(product, 1);
        shoppingCart.addItem(item);
        // Verify if the total price is 400.0
        assertEquals(400.0, shoppingCart.calculateTotal(), "Total price should be equal to the price of one product.");
    }

    /*Special Value Testing*/
    @Test
    public void testCalculateTotalWithEmptyCart() {
        // Boundary case: Shopping cart is empty
        // Verify if the total price is 0
        assertEquals(0.0, shoppingCart.calculateTotal(), "Total price should be 0 when the cart is empty.");
    }

}
