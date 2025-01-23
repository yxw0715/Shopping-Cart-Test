package onlineshopping;

public class PaymentService {
    public boolean processPayment(String creditCardNumber, double amount) throws Exception {
        // Simulating payment processing
        if (creditCardNumber.length() != 16 && amount <= 0) {
            throw new Exception("Payment failed: Invalid card or amount.");
        }
        return true;
    }
}

