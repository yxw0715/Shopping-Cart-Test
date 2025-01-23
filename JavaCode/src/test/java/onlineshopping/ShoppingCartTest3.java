package onlineshopping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartTest3 {

    @Mock
    private DiscountService discountService;
    @InjectMocks
    private ShoppingCart shoppingCart;

    @BeforeEach
    public void setUp() {
        // Use Mockito to mock DiscountService
        discountService = Mockito.mock(DiscountService.class);
        shoppingCart = new ShoppingCart(new Customer("Alice", CustomerType.REGULAR), discountService);

        // Completely mock the applyDiscount method logic, including existing error conditions
        when(discountService.applyDiscount(anyDouble(), any(), anyList(), any())).thenAnswer(invocation -> {
            double total = invocation.getArgument(0);
            List<CartItem> cartItems = invocation.getArgument(2);
            double discount = 0;

            // Mock the current bundle discount logic
            for (CartItem item : cartItems) {
                if (item.getProduct().getName().equals("Mouse")) {
                    boolean hasLaptop = cartItems.stream()
                            .filter(i -> i.getProduct().getName().equals("Laptop"))
                            .count() > 1; // Maintain erroneous condition: requires more than one laptop
                    if (hasLaptop) {
                        total -= item.getProduct().getPrice() * 0.05; // 5% off the mouse
                    }
                }
            }
            return total * (1 - discount);
        });
    }

    /* Normal Value Testing */
    @Test
    public void testBundleDiscountWithOneLaptopAndOneMouse() {
        // Boundary case: Shopping cart contains one laptop and one mouse
        Product laptop = new Product("Laptop", 1000.0, 5);
        Product mouse = new Product("Mouse", 100.0, 20);
        shoppingCart.addItem(new CartItem(laptop, 1));
        shoppingCart.addItem(new CartItem(mouse, 1));

        // Calculate final price, mouse should get 5% discount
        double finalPrice = shoppingCart.calculateFinalPrice();

        // Verify if final price is 1095 (1000 + 100 * 0.95)
        assertEquals(1095, finalPrice, "Total price should reflect 5% discount on mouse when bought with one laptop.");
    }

    @Test
    public void testBundleDiscountWithTwoLaptopsAndTwoMice() {
        // Normal case: Shopping cart contains 2 laptops and 2 mice
        Product laptop = new Product("Laptop", 1000.0, 5);
        Product mouse = new Product("Mouse", 100.0, 20);
        shoppingCart.addItem(new CartItem(laptop, 2));
        shoppingCart.addItem(new CartItem(mouse, 2));

        // Calculate final price, both mice should get 5% discount
        double finalPrice = shoppingCart.calculateFinalPrice();

        // Verify if final price is 2110 (2 * 1000 + 2 * 100 * 0.95)
        assertEquals(2190.0, finalPrice, "Total price should reflect 5% discount on both mice when bought with two laptops.");
    }

    @Test
    public void testNoBundleDiscountWithOnlyMiceOrOnlyLaptops() {
        // No discount when the shopping cart contains only multiple mice or multiple laptops
        Product laptop = new Product("Laptop", 1000.0, 5);
        Product mouse = new Product("Mouse", 100.0, 20);

        // Only mice
        shoppingCart.addItem(new CartItem(mouse, 2));
        assertEquals(200.0, shoppingCart.calculateFinalPrice(), "Total price should be 100.0 when only mice are in the cart with no bundle discount.");

        // Clear cart and add only laptops
        shoppingCart = new ShoppingCart(new Customer("Bob", CustomerType.REGULAR), discountService);
        shoppingCart.addItem(new CartItem(laptop, 2));
        assertEquals(2000.0, shoppingCart.calculateFinalPrice(), "Total price should be 2000.0 when only laptops are in the cart with no bundle discount.");
    }

    /* Special Case Testing */
    @Test
    public void testBundleDiscountWithMultipleMiceAndOneLaptop() {
        // Special case: Shopping cart contains multiple mice and one laptop
        Product laptop = new Product("Laptop", 1000.0, 5);
        Product mouse = new Product("Mouse", 100.0, 20);
        shoppingCart.addItem(new CartItem(laptop, 1));
        shoppingCart.addItem(new CartItem(mouse, 3));

        // Calculate final price, only one mouse should get 5% discount
        double finalPrice = shoppingCart.calculateFinalPrice();

        // Verify if final price is 1295 (1000 + 100 * 0.95 + 2 * 100)
        assertEquals(1295.0, finalPrice, "Total price should reflect 5% discount on one mouse when bought with one laptop.");
    }

    @Test
    public void testBundleDiscountWithMultipleLaptopsAndOneMouse() {
        // Special case: Shopping cart contains multiple laptops and one mouse
        Product laptop = new Product("Laptop", 1000.0, 5);
        Product mouse = new Product("Mouse", 100.0, 20);
        shoppingCart.addItem(new CartItem(laptop, 3));
        shoppingCart.addItem(new CartItem(mouse, 1));

        // Calculate final price, mouse should get 5% discount
        double finalPrice = shoppingCart.calculateFinalPrice();

        // Verify if final price is 3095 (3 * 1000 + 100 * 0.95)
        assertEquals(3095.0, finalPrice, "Total price should reflect 5% discount on one mouse when bought with multiple laptops.");
    }
}
