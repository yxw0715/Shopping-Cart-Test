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
public class ShoppingCartTest6 {

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
            String couponCode = invocation.getArgument(3);
            // Mock the coupon discount logic
            double discount = 0;
            // Apply coupon code discounts, only if a valid coupon code is provided
            if (couponCode != null && !couponCode.isEmpty()) {
                // Example: If coupon code is "DISCOUNT10", give 10% off
                if (couponCode.equals("DISCOUNT10")) {
                    discount += 0.10;
                } else if (couponCode.equals("SAVE50")) {
                    total -= 50; // Fixed amount discount of $50
                }
            }
            return total * (1 - discount);
        });
    }

    /* Normal Value Testing */
    @Test
    public void testDiscount10CouponCode() {
        // Normal case: Use "DISCOUNT10" coupon code, 10% discount should be applied
        shoppingCart = new ShoppingCart(new Customer("Alice", CustomerType.REGULAR), discountService);
        shoppingCart.applyCouponCode("DISCOUNT10");
        Product product = new Product("Cup", 400.0, 10);
        shoppingCart.addItem(new CartItem(product, 1));

        // Verify if the final price is 360.0 (400 * 0.90)
        assertEquals(360.0, shoppingCart.calculateFinalPrice(), "Total price should be 360.0 with 10% discount for DISCOUNT10 coupon.");
    }

    @Test
    public void testSave50CouponCode() {
        // Normal case: Use "SAVE50" coupon code, fixed £50 discount should be applied
        shoppingCart = new ShoppingCart(new Customer("Alice", CustomerType.REGULAR), discountService);
        shoppingCart.applyCouponCode("SAVE50");
        Product product = new Product("Cup", 400.0, 10);
        shoppingCart.addItem(new CartItem(product, 1));

        // Verify if the final price is 350.0 (400 - 50)
        assertEquals(350.0, shoppingCart.calculateFinalPrice(), "Total price should be 350.0 with SAVE50 coupon applied.");
    }

    /*Special Value Testing*/
    @Test
    public void testCannotUseMultipleCoupons() {
        // Normal case: Attempt to use both "DISCOUNT10" and "SAVE50", only one coupon should apply
        shoppingCart = new ShoppingCart(new Customer("Alice", CustomerType.REGULAR), discountService);
        shoppingCart.applyCouponCode("DISCOUNT10");
        shoppingCart.applyCouponCode("SAVE50"); // Overwrites the previous coupon
        Product product = new Product("Cup", 400.0, 10);
        shoppingCart.addItem(new CartItem(product, 1));

        // Verify if the final price is 350.0 (400 - 50)
        assertEquals(350.0, shoppingCart.calculateFinalPrice(), "Total price should be 350.0 with only SAVE50 coupon applied.");
    }

    /* Boundary Value Testing */
    @Test
    public void testSave50CouponCodeWithCartTotalEqualTo50() {
        // Boundary case: Shopping cart total equal to £50, use "SAVE50" coupon code
        shoppingCart = new ShoppingCart(new Customer("Alice", CustomerType.REGULAR), discountService);
        shoppingCart.applyCouponCode("SAVE50");
        Product product = new Product("Book", 50.0, 10);
        shoppingCart.addItem(new CartItem(product, 1));

        // Verify if the final price is 0.0 (50 - 50)
        assertEquals(0.0, shoppingCart.calculateFinalPrice(), "Total price should be 0.0 with SAVE50 coupon applied on cart total of £50.");
    }

    @Test
    public void testSave50CouponCodeWithCartTotalLessThan50() {
        // Boundary case: Shopping cart total less than £50, use "SAVE50" coupon code
        shoppingCart = new ShoppingCart(new Customer("Alice", CustomerType.REGULAR), discountService);
        shoppingCart.applyCouponCode("SAVE50");
        Product product = new Product("Pencil", 30.0, 10);
        shoppingCart.addItem(new CartItem(product, 1));

        // Verify if the final price is 0.0 (30 - 50, but should not be negative)
        assertEquals(0.0, shoppingCart.calculateFinalPrice(), "Total price should be 0.0 with SAVE50 coupon applied on cart total less than £50.");
    }
}

