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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartTest7 {

    @Mock
    private DiscountService discountService;
    @InjectMocks
    private ShoppingCart shoppingCart;

    @BeforeEach
    public void setUp() {
        // Initialize test environment
        discountService = Mockito.mock(DiscountService.class);
        shoppingCart = new ShoppingCart(new Customer("Alice", CustomerType.REGULAR), discountService);

        // Mock the applyPromotionDiscount method leniently to avoid unnecessary stubbing exceptions
        lenient().when(discountService.applyPromotionDiscount(anyDouble())).thenAnswer(invocation -> {
            double total = invocation.getArgument(0);
            if (shoppingCart.isPromotionActive) {
                return total * 0.75; // Apply 25% discount for promotion if active
            }
            return total; // No discount if promotion is not active
        });

        // Mock the applyDiscount method leniently to avoid calling unimplemented methods
        lenient().when(discountService.applyDiscount(anyDouble(), any(), anyList(), any())).thenAnswer(invocation -> {
            double total = invocation.getArgument(0);
            return total; // No additional discount for simplicity
        });
    }

    /* Normal Value Testing */
    @Test
    public void testActivatePromotionDiscount() {
        // Normal case: Activate promotion discount, cart total should have 25% discount
        shoppingCart.setPromotionActive(true);
        Product product = new Product("Cup", 400.0, 10);
        shoppingCart.addItem(new CartItem(product, 1));

        // Verify if the final price is 300.0 (400 * 0.75)
        assertEquals(300.0, shoppingCart.calculateFinalPrice(), "Total price should be 30.0 with promotion discount activated.");
    }

    @Test
    public void testDeactivatePromotionDiscount() {
        // Normal case: Deactivate promotion discount, cart total should have no discount
        shoppingCart.setPromotionActive(false);
        Product product = new Product("Cup", 400.0, 10);
        shoppingCart.addItem(new CartItem(product, 1));

        // Verify if the final price is 400.0
        assertEquals(400.0, shoppingCart.calculateFinalPrice(), "Total price should be 400.0 with promotion discount deactivated.");
    }

    /* Special Value Testing */
    @Test
    public void testActivateThenDeactivatePromotionDiscount() {
        // Special case: Activate then deactivate promotion discount, verify total price recovery
        shoppingCart.setPromotionActive(true);
        Product product = new Product("Cup", 400.0, 10);
        shoppingCart.addItem(new CartItem(product, 1));

        // Verify the price after activating promotion
        assertEquals(300.0, shoppingCart.calculateFinalPrice(), "Total price should be 300.0 after activating promotion discount.");

        // Deactivate promotion and recheck
        shoppingCart.setPromotionActive(false);

        // Verify the price after deactivating promotion
        assertEquals(400.0, shoppingCart.calculateFinalPrice(), "Total price should be 400.0 after deactivating promotion discount.");
    }
}
