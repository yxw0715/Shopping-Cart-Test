package onlineshopping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShoppingCartTest1 {

    private ShoppingCart shoppingCart;
    private Product product;
    private DiscountService discountService;

    @BeforeEach
    public void setUp() {
        // Initialize the test environment
        discountService = new DiscountService();
        shoppingCart = new ShoppingCart(new Customer("Alice", CustomerType.REGULAR), discountService);
        product = new Product("Cup", 400.0, 10);
    }
    /*Normal Value Testing*/
    @Test
    public void testAddItemToCart() {
        // Add products to cart
        CartItem cartItem = new CartItem(product, 1);
        shoppingCart.addItem(cartItem);

        // Verify that the number of products in the shopping cart is 1
        assertEquals(1, shoppingCart.getItems().size());
        // Verify that the first product in the shopping cart is the one we added
        assertEquals("Cup", shoppingCart.getItems().get(0).getProduct().getName());
    }
    @Test
    public void testAddMultipleProducts() {
        // Test adding multiple different products
        Product laptop = new Product("Laptop", 1000.0, 5);
        Product mouse = new Product("Mouse", 50.0, 20);

        // ensuring that each product is correctly added to the cart
        shoppingCart.addItem(new CartItem(product, 1));
        shoppingCart.addItem(new CartItem(laptop, 2));
        shoppingCart.addItem(new CartItem(mouse, 1));

        // Verify that the number of products in the shopping cart is 3
        assertEquals(3, shoppingCart.getItems().size(), "Cart should contain three different products");
    }

    /*Boundary Value Testing*/
    @Test
    public void testAddProductWithMaxStock() {
        // Verify adding a product with maximum available stock
        CartItem cartItem = new CartItem(product, product.getStock());
        shoppingCart.addItem(cartItem);

        // ensuring correct quantity in the cart
        assertEquals(1, shoppingCart.getItems().size(), "Cart should contain one item with maximum available stock");
        assertEquals(product.getStock(), shoppingCart.getItems().get(0).getQuantity(), "Quantity should be equal to stock limit");
    }

    /*Special Value Testing*/
    @Test
    public void testAddProductExceedingStock() {
        // Test if adding a product with quantity exceeding available stock throws an appropriate exception
        int exceedingQuantity = product.getStock() + 1;
        CartItem cartItem = new CartItem(product, exceedingQuantity);

        Exception exception = null;
        try {
            shoppingCart.addItem(cartItem);
        } catch (IllegalArgumentException e) {
            exception = e;
        }

        // Verify that the exception is thrown
        assertEquals("Not enough stock available", exception != null ? exception.getMessage() : "", "Should throw exception for insufficient stock");

        //assertThrows(IllegalArgumentException.class, () -> shoppingCart.addItem(cartItem), "Should throw exception for exceeding stock");
        assertNotEquals(product.getStock() + 1, shoppingCart.getItems().size());
    }

}
