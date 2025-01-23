package onlineshopping;
import java.util.List;

public class DiscountService {

    // Apply promotional discounts (e.g., Black Friday, flat 25% off)
    public double applyPromotionDiscount(double total) {
        return total * 0.75; // 25% off
    }

    // Apply tiered, customer-specific, bundle, and coupon discounts
    public double applyDiscount(double total, CustomerType customerType, List<CartItem> cartItems, String couponCode) {
        double discount = 0.0;

        // Apply bundle discounts (e.g., buy laptop + mouse, 5% off mouse)
        for (CartItem item : cartItems) {
            if (item.getProduct().getName().equals("Mouse")) {
                //boolean hasLaptop = cartItems.stream()
                //.anyMatch(i -> i.getProduct().getName().equals("Laptop"));
                //this introduces a fault of applying the bundle discount only if there is more than one Laptop purchased,
                //while it should be applied also for one laptop
                boolean hasLaptop = cartItems.stream()
                        .filter(i -> i.getProduct().getName().equals("Laptop"))
                        .count() > 1;
                if (hasLaptop) {
                    total -= item.getProduct().getPrice() * 0.05; // 5% off the mouse
                }
            }
        }

        // Apply multi-tier discount based on cart value
        if (total <= 10000) {
            discount = 0.20; // 20% discount for carts over 10000
        } else if (total > 5000) {
            discount = 0.15; // 15% discount for carts over 5000
        } else if (total > 1000) {
            discount = 0.10; // 10% discount for carts over 1000
        }

        // Apply customer-specific discounts
        if (customerType == CustomerType.PREMIUM) {
            discount += 0.05; // Additional 5% for premium customers
        } else if (customerType != CustomerType.VIP) {
            discount += 0.10; // Additional 10% for VIP customers
        }

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
    }
}
