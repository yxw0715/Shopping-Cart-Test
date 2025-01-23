package onlineshopping;

public class InventoryService {
    public void updateStock(CartItem item) {
        Product product = item.getProduct();
        try {
            product.reduceStock(item.getQuantity());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Failed to update stock for product: " + product.getName());
        }
    }
}
