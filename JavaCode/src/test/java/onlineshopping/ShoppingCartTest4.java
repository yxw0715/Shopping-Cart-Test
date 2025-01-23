package onlineshopping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartTest4 {

    @Mock
    private DiscountService discountService;
    @InjectMocks
    private ShoppingCart shoppingCart;

    @BeforeEach
    public void setUp() {
        // Initialize test environment
        discountService = Mockito.mock(DiscountService.class);
        shoppingCart = new ShoppingCart(new Customer("Alice", CustomerType.REGULAR), discountService);

        // Mock the applyDiscount method to focus only on tiered discount testing
        when(discountService.applyDiscount(anyDouble(), any(), anyList(), any())).thenAnswer(invocation -> {
            double total = invocation.getArgument(0);
            double discount = 0;
            // Mock the tiered discount logic
            // Apply multi-tier discount based on cart value
            if (total <= 10000) {
                discount = 0.20; // 20% discount for carts over 10000
            } else if (total > 5000) {
                discount = 0.15; // 15% discount for carts over 5000
            } else if (total > 1000) {
                discount = 0.10; // 10% discount for carts over 1000
            }

            return total * (1 - discount);
        });
    }

    /* Normal Value Testing */
    @Test
    public void testTieredDiscountWithAmountLessThan1000() {
        // Normal case: Shopping cart total is less than 1000, no discount should be applied
        Product product = new Product("Cup", 400.0, 10);
        shoppingCart.addItem(new CartItem(product, 1));

        // Verify if the total price is 500.0
        assertEquals(400.0, shoppingCart.calculateFinalPrice(), "Total price should be 400.0 without any discount applied.");
    }

    @Test
    public void testTieredDiscountWithAmountBetween1000And5000() {
        // Normal case: Shopping cart total is between 1000 and 5000, 10% discount should be applied
        Product product = new Product("Laptop", 2000.0, 5);
        shoppingCart.addItem(new CartItem(product, 1));

        // Verify if the total price is 1800.0 (2000 * 0.90)
        assertEquals(1800.0, shoppingCart.calculateFinalPrice(), "Total price should be 1800.0 with 10% discount applied.");
    }

    @Test
    public void testTieredDiscountWithAmountBetween5000And10000() {
        // Normal case: Shopping cart total is between 5000 and 10000, 15% discount should be applied
        Product product = new Product("Bed", 7000.0, 5);
        shoppingCart.addItem(new CartItem(product, 1));

        // Verify if the total price is 5950.0 (7000 * 0.85)
        assertEquals(5950.0, shoppingCart.calculateFinalPrice(), "Total price should be 5950.0 with 15% discount applied.");
    }

    @Test
    public void testTieredDiscountWithAmountGreaterThan10000() {
        // Normal case: Shopping cart total is greater than 10000, 20% discount should be applied
        Product product = new Product("Luxury Car", 15000.0, 1);
        shoppingCart.addItem(new CartItem(product, 1));

        // Verify if the total price is 12000.0 (15000 * 0.80)
        assertEquals(12000.0, shoppingCart.calculateFinalPrice(), "Total price should be 12000.0 with 20% discount applied.");
    }

    /* Boundary Value Testing */
    @Test
    public void testTieredDiscountWithAmountEqualTo1000() {
        // Boundary case: Shopping cart total is equal to 1000, 10% discount should be applied
        Product product = new Product("Book", 1000.0, 5);
        shoppingCart.addItem(new CartItem(product, 1));

        // Verify if the total price is 900.0 (1000 * 0.90)
        assertEquals(900.0, shoppingCart.calculateFinalPrice(), "Total price should be 900.0 with 10% discount applied.");
    }

    @Test
    public void testTieredDiscountWithAmountEqualTo5000() {
        // Boundary case: Shopping cart total is equal to 5000, 15% discount should be applied
        Product product = new Product("Gaming Setup", 5000.0, 5);
        shoppingCart.addItem(new CartItem(product, 1));

        // Verify if the total price is 4250.0 (5000 * 0.85)
        assertEquals(4250.0, shoppingCart.calculateFinalPrice(), "Total price should be 4250.0 with 15% discount applied.");
    }

    @Test
    public void testTieredDiscountWithAmountEqualTo10000() {
        // Boundary case: Shopping cart total is equal to 10000, 20% discount should be applied
        Product product = new Product("Moto", 10000.0, 3);
        shoppingCart.addItem(new CartItem(product, 1));

        // Verify if the total price is 8000.0 (10000 * 0.80)
        assertEquals(8000.0, shoppingCart.calculateFinalPrice(), "Total price should be 8000.0 with 20% discount applied.");
    }
}
