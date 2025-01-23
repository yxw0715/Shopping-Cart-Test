package onlineshopping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartTest8 {

    @Spy
    private DiscountService discountServiceSpy;

    @InjectMocks
    private ShoppingCart shoppingCart;

    @BeforeEach
    public void setUp() {
        // Initialize DiscountService with Spy to monitor method calls
        discountServiceSpy = spy(new DiscountService());
        shoppingCart = new ShoppingCart(new Customer("Alice", CustomerType.VIP), discountServiceSpy);

        lenient().when(discountServiceSpy.applyPromotionDiscount(anyDouble())).thenAnswer(invocation -> {
            double total = invocation.getArgument(0);
            if (shoppingCart.isPromotionActive) {
                System.out.println("<!-------applyPromotionDiscount-------!>");
                return total * 0.75; // Apply 25% discount for promotion if active
            }
            return total; // No discount if promotion is not active
        });

        // Mock the applyDiscount method to avoid incorrect logic affecting the tests
        lenient().when(discountServiceSpy.applyDiscount(anyDouble(), any(), anyList(), anyString())).thenAnswer(invocation -> {
            double total = invocation.getArgument(0);
            CustomerType customerType = invocation.getArgument(1);
            double discount = 0;

            // Apply correct discount logic for testing purposes
            // Apply bundle discounts
            int laptopCount = (int) shoppingCart.getItems().stream()
                    .filter(i -> i.getProduct().getName().equals("Laptop"))
                    .mapToInt(CartItem::getQuantity)
                    .sum();
            for (CartItem item : shoppingCart.getItems()) {
                if (item.getProduct().getName().equals("Mouse")) {
                    int mouseCount = item.getQuantity();
                    // Calculate the number of pairs (number of mice discounted in pairs)
                    // taking the smaller of the number of laptops and the number of mice
                    int discountedMouseCount = Math.min(laptopCount, mouseCount);
                    if (discountedMouseCount > 0) {
                        total -= item.getProduct().getPrice() * 0.05 * discountedMouseCount; // 5% off for the paired mice
                        System.out.println("<!-------applyBundleDiscount-------!>");
                        System.out.println("After that total="+total);
                    }
                }
            }

            // Apply multi-tier discount based on cart value
            if (total > 10000) {
                System.out.println( "<!-------applyMultiTierDiscount3-------!>");
                discount = 0.20; // 20% discount for carts over 10000
                System.out.println("After that discount="+discount);
            } else if (total > 5000) {
                System.out.println( "<!-------applyMultiTierDiscount2-------!>");
                discount = 0.15; // 15% discount for carts over 5000
                System.out.println("After that discount="+discount);
            } else if (total > 1000) {
                System.out.println( "<!-------applyMultiTierDiscount1-------!>");
                discount = 0.10; // 10% discount for carts over 1000
                System.out.println("After that discount="+discount);
            }

            // Apply customer-specific discounts
            if (customerType == CustomerType.PREMIUM) {
                System.out.println( "<!-------applyCustomerSpecificDiscount PREMIUM-------!>");
                discount += 0.05; // Additional 5% for premium customers
                System.out.println("After that discount="+discount);
            } else if (customerType == CustomerType.VIP) {
                System.out.println( "<!-------applyCustomerSpecificDiscount VIP-------!>");
                discount += 0.10; // Additional 10% for VIP customers
                System.out.println("After that discount="+discount);
            }

            // Apply coupon code discounts, if provided
            String couponCode = invocation.getArgument(3);
            if (couponCode != null && !couponCode.isEmpty()) {
                if (couponCode.equals("DISCOUNT10")) {
                    System.out.println( "<!-------applyCouponDiscount DISCOUNT10-------!>");
                    discount += 0.10;
                    System.out.println("After that discount="+discount);
                } else if (couponCode.equals("SAVE50")) {
                    System.out.println( "<!-------applyCouponDiscount SAVE50-------!>");
                    total -= 50; // Fixed amount discount of $50
                    System.out.println("After that total="+total);
                }
            }

            return total * (1 - discount);
        });
    }

    /* Normal Value Testing */
    @Test
    public void testAllDiscounts() {
        shoppingCart.setPromotionActive(true);
        shoppingCart.applyCouponCode("SAVE50");

        Product laptop = new Product("Laptop", 1000.0, 5);
        Product mouse = new Product("Mouse", 100.0, 20);
        shoppingCart.addItem(new CartItem(laptop, 1));
        shoppingCart.addItem(new CartItem(mouse, 1));
        // Calculate final price
        double actualFinalPrice = shoppingCart.calculateFinalPrice();
        // Verify that the calculated final price matches the expected final price
        assertEquals(574.75, actualFinalPrice, 0.01, "Final price should include both other discounts and promotion discount in the correct order.");
    }

    @Test
    public void testWithoutPromotionDiscounts() {
        shoppingCart.applyCouponCode("SAVE50");

        Product laptop = new Product("Laptop", 1000.0, 5);
        Product mouse = new Product("Mouse", 100.0, 20);
        shoppingCart.addItem(new CartItem(laptop, 1));
        shoppingCart.addItem(new CartItem(mouse, 1));
        // Calculate final price
        double actualFinalPrice = shoppingCart.calculateFinalPrice();
        // Verify that the calculated final price matches the expected final price
        assertEquals(836.0, actualFinalPrice, 0.01, "Final price should include both other discounts and promotion discount in the correct order.");
    }
}

//        // Calculate final price
//        double priceAfterOtherDiscounts = discountServiceSpy.applyDiscount(shoppingCart.calculateTotal(), CustomerType.VIP, shoppingCart.getItems(), "DISCOUNT10");
//        double expectedFinalPrice = discountServiceSpy.applyPromotionDiscount(priceAfterOtherDiscounts) ; // Apply promotion discount
//        double actualFinalPrice = shoppingCart.calculateFinalPrice();
//
//        // Verify that the calculated final price matches the expected final price
//        //assertEquals(expectedFinalPrice, actualFinalPrice, 0.01, "Final price should include both other discounts and promotion discount in the correct order.");
//
//        // Use InOrder to verify the correct order of discount application
//        InOrder inOrder = inOrder(discountServiceSpy);
//        // Verify that applyDiscount is called before applyPromotionDiscount
//        inOrder.verify(discountServiceSpy).applyDiscount(anyDouble(), any(), anyList(), anyString());
//        inOrder.verify(discountServiceSpy).applyPromotionDiscount(anyDouble());
