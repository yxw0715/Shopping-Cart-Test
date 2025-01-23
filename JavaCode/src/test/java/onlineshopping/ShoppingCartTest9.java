package onlineshopping;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartTest9 {

    @Spy
    private DiscountService discountServiceSpy;

    @InjectMocks
    private ShoppingCart shoppingCart;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        // Initialize DiscountService with Spy to monitor method calls
        discountServiceSpy = spy(new DiscountService());
        shoppingCart = new ShoppingCart(new Customer("Alice", CustomerType.REGULAR), discountServiceSpy);

        // Redirect System.out to capture print statements
        System.setOut(new PrintStream(outContent));

        // Mock the applyDiscount method to avoid incorrect logic affecting the tests
        lenient().when(discountServiceSpy.applyDiscount(anyDouble(), any(), anyList(), any())).thenAnswer(invocation -> {
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
                    }
                }
            }

            // Apply multi-tier discount based on cart value
            if (total > 10000) {
                discount = 0.20; // 20% discount for carts over 10000
            } else if (total > 5000) {
                discount = 0.15; // 15% discount for carts over 5000
            } else if (total > 1000) {
                discount = 0.10; // 10% discount for carts over 1000
            }

            // Apply customer-specific discounts
            if (customerType == CustomerType.PREMIUM) {
                discount += 0.05; // Additional 5% for premium customers
            } else if (customerType == CustomerType.VIP) {
                discount += 0.10; // Additional 10% for VIP customers
            }

            // Apply coupon code discounts, if provided
            String couponCode = invocation.getArgument(3);
            if (couponCode != null && !couponCode.isEmpty()) {
                if (couponCode.equals("DISCOUNT10")) {
                    discount += 0.10;
                } else if (couponCode.equals("SAVE50")) {
                    total -= 50; // Fixed amount discount of $50
                }
            }

            return total * (1 - discount);
        });
    }

    @AfterEach
    public void tearDown() {
        // Reset System.out to original
        System.setOut(originalOut);
        // Print the captured output for verification
        System.out.println(outContent.toString());
        // Clear the captured output
        outContent.reset();
    }

    /* Normal Value Testing */
    @Test
    public void testReceiptWithSingleItemNoDiscount() {
        Product product = new Product("Cup", 400.0, 10);
        shoppingCart.addItem(new CartItem(product, 1));

        // Print receipt
        shoppingCart.printReceipt();
        String receiptOutput = outContent.toString();

        // Verify that receipt contains the item information
        assertTrue(receiptOutput.contains("Cup - 1 x $400.0"), "Receipt should contain Cup details.");
        assertTrue(receiptOutput.contains("Total before discount: $400.0"), "Receipt should contain total before discount.");
        assertTrue(receiptOutput.contains("Final price after discounts: $400.0"), "Receipt should contain final price without any discount.");
    }

    @Test
    public void testReceiptWithSingleItemAndDiscounts() {
        shoppingCart = new ShoppingCart(new Customer("Bob", CustomerType.VIP), discountServiceSpy);
        shoppingCart.applyCouponCode("DISCOUNT10");

        Product product = new Product("Cup", 400.0, 10);
        shoppingCart.addItem(new CartItem(product, 3));

        // Print receipt
        shoppingCart.printReceipt();

        String receiptOutput = outContent.toString();

        // Verify that receipt contains the item information and discount details
        assertTrue(receiptOutput.contains("Cup - 3 x $400.0"), "Receipt should contain Cup details.");
        assertTrue(receiptOutput.contains("Total before discount: $1200.0"), "Receipt should contain total before discount.");
        //1200*(1-0.1-0.1-0.1)(over1000/VIP/coupon)=840
        assertTrue(receiptOutput.contains("Final price after discounts: $840.0"), "Receipt should contain final price after discounts.");
    }

    @Test
    public void testReceiptWithMultipleItemsAndDiscounts() {
        shoppingCart = new ShoppingCart(new Customer("Bob", CustomerType.VIP), discountServiceSpy);
        shoppingCart.applyCouponCode("SAVE50");

        Product laptop = new Product("Laptop", 1000.0, 5);
        Product mouse = new Product("Mouse", 100.0, 20);
        shoppingCart.addItem(new CartItem(laptop, 1));
        shoppingCart.addItem(new CartItem(mouse, 1));

        // Print receipt
        shoppingCart.printReceipt();

        String receiptOutput = outContent.toString();

        // Verify that receipt contains all necessary information
        assertTrue(receiptOutput.contains("Laptop - 1 x $1000.0"), "Receipt should contain laptop details.");
        assertTrue(receiptOutput.contains("Mouse - 1 x $100.0"), "Receipt should contain mouse details.");
        assertTrue(receiptOutput.contains("Total before discount: $1100.0"), "Receipt should contain total before discount.");
        //outcome of applyDicount should be 836
        assertTrue(receiptOutput.contains("Final price after discounts: $836.0"), "Receipt should contain final price after discounts.");
    }

    /* Boundary Value Testing */
    @Test
    public void testReceiptWithEmptyCart() {
        // Print receipt for empty cart
        shoppingCart.printReceipt();

        String receiptOutput = outContent.toString();

        // Verify that receipt indicates the cart is empty
        assertTrue(receiptOutput.contains("Shopping cart is empty"), "Receipt should indicate that the shopping cart is empty.");
    }
}
