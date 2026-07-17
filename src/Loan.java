import java.math.BigDecimal;

public class Loan {
    // Auto-incremented ID for demo purposes (in-memory only).
    private static int nextLoanId = 1;

    private final int loanId;
    private final String customerName;
    private final BigDecimal loanAmount;
    private BigDecimal paidAmount;
    private final boolean initialOverpaymentAdjusted;

    public Loan(String customerName, BigDecimal loanAmount, BigDecimal paidAmount) {
        // Constructor guards keep every Loan object valid from creation.
        if (customerName == null || customerName.trim().isEmpty() || !isValidName(customerName.trim())) {
            throw new IllegalArgumentException("Customer name must contain letters and spaces only.");
        }
        if (loanAmount == null || loanAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Loan amount must be a positive number.");
        }
        if (paidAmount == null || paidAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial paid amount must be a non-negative number.");
        }

        this.loanId = nextLoanId++;
        this.customerName = customerName.trim();
        this.loanAmount = loanAmount;
        this.paidAmount = paidAmount;

        // Clamp overpaid initial amounts; caller decides how to notify user.
        boolean adjusted = false;
        if (this.paidAmount.compareTo(this.loanAmount) > 0) {
            this.paidAmount = this.loanAmount;
            adjusted = true;
        }
        this.initialOverpaymentAdjusted = adjusted;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getLoanId() {
        return loanId;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public boolean isInitialOverpaymentAdjusted() {
        return initialOverpaymentAdjusted;
    }

    // Returns how much is still unpaid.
    public BigDecimal calculateRemainingBalance() {
        BigDecimal remaining = loanAmount.subtract(paidAmount);
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }
        return remaining;
    }

    // Returns loan status based on remaining balance.
    public String getLoanStatus() {
        if (calculateRemainingBalance().compareTo(BigDecimal.ZERO) == 0) {
            return "Completed";
        } else {
            return "Outstanding";
        }
    }

    // Adds repayment amount to paid amount.
    public boolean makePayment(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Repayment amount must be a positive number.");
        }
        if (calculateRemainingBalance().compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("This loan is already completed.");
        }

        paidAmount = paidAmount.add(amount);
        // Prevent balance from going negative when payment exceeds the remaining amount.
        if (paidAmount.compareTo(loanAmount) > 0) {
            paidAmount = loanAmount;
            return true;
        }
        return false;
    }

    private static boolean isValidName(String name) {
        return ValidationPatterns.NAME_PATTERN.matcher(name).matches();
    }

}
