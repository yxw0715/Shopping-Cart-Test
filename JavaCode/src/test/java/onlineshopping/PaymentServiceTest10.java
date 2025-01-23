package onlineshopping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentServiceTest10 {

    private PaymentService paymentService;

    @BeforeEach
    public void setUp() {
        paymentService = new PaymentService();
    }

    /* Normal Value Testing */
    @Test
    public void testValidTransaction() {
        // Normal transaction with valid card number and amount
        String validCreditCardNumber = "1234567890123456";
        double validAmount = 100.0;
        // Should not throw any exception
        assertDoesNotThrow(() -> paymentService.processPayment(validCreditCardNumber, validAmount));
    }

    /* Boundary Value Testing */
    @Test
    public void testCreditCardNumberTooShort() {
        // Credit card number is less than 16 digits
        String shortCreditCardNumber = "123456789012345"; // 15 digits
        Exception exception = assertThrows(Exception.class, () -> {
            paymentService.processPayment(shortCreditCardNumber, 100.0);
        });
        assertTrue(exception.getMessage().contains("Payment failed: Invalid card or amount."));
    }

    @Test
    public void testCreditCardNumberTooLong() {
        // Credit card number is more than 16 digits
        String longCreditCardNumber = "12345678901234567"; // 17 digits
        Exception exception = assertThrows(Exception.class, () -> {
            paymentService.processPayment(longCreditCardNumber, 100.0);
        });
        assertTrue(exception.getMessage().contains("Payment failed: Invalid card or amount."));
    }

    @Test
    public void testTransactionAmountIsZero() {
        // Transaction amount is zero
        String validCreditCardNumber = "1234567890123456";
        Exception exception = assertThrows(Exception.class, () -> {
            paymentService.processPayment(validCreditCardNumber, 0.0);
        });
        assertTrue(exception.getMessage().contains("Payment failed: Invalid card or amount."));
    }
    @Test
    public void testTransactionAmountIsNegative() {
        // Valid credit card number but transaction amount is negative
        String validCreditCardNumber = "1234567890123456";
        Exception exception = assertThrows(Exception.class, () -> {
            paymentService.processPayment(validCreditCardNumber, -50.0);
        });
        assertTrue(exception.getMessage().contains("Payment failed: Invalid card or amount."));
    }


    /* Special Value Testing */
    @Test
    public void testCreditCardNumberWrongAndAmountIsNegative() {
        // Credit card number is incorrect and transaction amount is negative
        String invalidCreditCardNumber = "123456789012"; // Invalid length
        Exception exception = assertThrows(Exception.class, () -> {
            paymentService.processPayment(invalidCreditCardNumber, -50.0);
        });
        assertTrue(exception.getMessage().contains("Payment failed: Invalid card or amount."));
    }
}
