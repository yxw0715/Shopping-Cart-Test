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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartTest5 {

    @Mock
    private DiscountService discountService;
    @InjectMocks
    private ShoppingCart shoppingCart;

    @BeforeEach
    public void setUp() {
        // Initialize test environment
        discountService = Mockito.mock(DiscountService.class);

        when(discountService.applyDiscount(anyDouble(), any(), anyList(), any())).thenAnswer(invocation -> {
            double total = invocation.getArgument(0);
            CustomerType customerType = invocation.getArgument(1);
            // Mock the customer discount logic
            double discount = 0;
            // Apply customer-specific discounts
            if (customerType == CustomerType.PREMIUM) {
                discount += 0.05; // Additional 5% for premium customers
            } else if (customerType != CustomerType.VIP) {
                discount += 0.10; // Additional 10% for VIP customers
            }
            return total * (1 - discount);
        });
    }

    /* Normal Value Testing */
    @Test
    public void testNoExtraDiscountForRegularCustomer() {
        // Normal case: Regular customer, no extra discount should be applied
        shoppingCart = new ShoppingCart(new Customer("Alice", CustomerType.REGULAR), discountService);
        Product product = new Product("Cup", 400.0, 10);
        shoppingCart.addItem(new CartItem(product, 1));
        // Verify if the final price is 400.0
        assertEquals(400.0, shoppingCart.calculateFinalPrice(), "Total price should be 400.0 without any extra discount for Regular customer.");
    }

    @Test
    public void testFivePercentDiscountForPremiumCustomer() {
        // Normal case: Premium customer, additional 5% discount should be applied
        shoppingCart = new ShoppingCart(new Customer("Bob", CustomerType.PREMIUM), discountService);
        Product product = new Product("Cup", 400.0, 10);
        shoppingCart.addItem(new CartItem(product, 1));

        // Verify if the final price is 380.0 (400 * 0.95)
        assertEquals(380.0, shoppingCart.calculateFinalPrice(), "Total price should be 380.0 with 5% discount for Premium customer.");
    }

    @Test
    public void testTenPercentDiscountForVIPCustomer() {
        // Normal case: VIP customer, additional 10% discount should be applied
        shoppingCart = new ShoppingCart(new Customer("Lucy", CustomerType.VIP), discountService);
        Product product = new Product("Cup", 400.0, 10);
        shoppingCart.addItem(new CartItem(product, 1));

        // Verify if the final price is 400.0 (360 * 0.90)
        assertEquals(360.0, shoppingCart.calculateFinalPrice(), "Total price should be 360.0 with 10% discount for VIP customer.");
    }

}
