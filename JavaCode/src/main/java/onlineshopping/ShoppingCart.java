package onlineshopping;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private Customer customer;
    private List<CartItem> items;
    private DiscountService discountService;
    private String couponCode;
    public boolean isPromotionActive;

    public ShoppingCart(Customer customer, DiscountService discountService) {
        this.customer = customer;
        this.items = new ArrayList<>();
        this.discountService = discountService;
        this.isPromotionActive = false;  // Default promotion status is inactive
    }

    // Add an item to the shopping cart
    public void addItem(CartItem item) {
        items.add(item);
    }

    // Remove an item from the shopping cart
    public void removeItem(CartItem item) {
        items.remove(item);
    }

    // Set a coupon code for discount
    public void applyCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    // Activate or deactivate a promotion
    public void setPromotionActive(boolean isActive) {
        this.isPromotionActive = isActive;
    }

    // Calculate the total price before any discounts
    public double calculateTotal() {
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }

    // Calculate the final price after applying discounts, promotions, and coupon codes
    public double calculateFinalPrice() {
        double total = calculateTotal();

        // Apply promotion if active
        if (isPromotionActive) {
            total = discountService.applyPromotionDiscount(total);
        } else {
            // Apply multi-tier discount, customer type discount, and bundle discount
            total = discountService.applyDiscount(total, customer.getCustomerType(), items, couponCode);
        }

        return total;
    }

    // Print a detailed breakdown of the cart
    public void printReceipt() {
        System.out.println("----- Shopping Cart Receipt -----");
        for (CartItem item : items) {
            System.out.println(item.getProduct().getName() + " - " + item.getQuantity() + " x $" + item.getProduct().getPrice());
        }
        System.out.println("---------------------------------");
        System.out.println("Total before discount: $" + calculateTotal());
        System.out.println("Final price after discounts: $" + calculateFinalPrice());
    }
    public List<CartItem> getItems() {
        return items;
    }
}
