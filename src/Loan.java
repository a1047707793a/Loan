import java.math.BigDecimal;

public class Loan {
    private static final LoanRules DEFAULT_RULES = DefaultLoanRules.getInstance();

    // Auto-incremented ID for demo purposes (in-memory only).
    private static int nextLoanId = 1;

    private final int loanId;
    private final String customerName;
    private final BigDecimal loanAmount;
    private BigDecimal paidAmount;
    private final boolean initialOverpaymentAdjusted;
    private final LoanRules loanRules;

    public Loan(String customerName, BigDecimal loanAmount, BigDecimal paidAmount) {
        this(customerName, loanAmount, paidAmount, DEFAULT_RULES);
    }

    public Loan(String customerName, BigDecimal loanAmount, BigDecimal paidAmount, LoanRules loanRules) {
        // Constructor guards keep every Loan object valid from creation.
        this.loanRules = loanRules == null ? DEFAULT_RULES : loanRules;
        this.loanRules.validateCustomerName(customerName);
        this.loanRules.validateLoanAmount(loanAmount);
        this.loanRules.validateInitialPaidAmount(paidAmount);

        this.loanId = nextLoanId++;
        this.customerName = customerName.trim();
        this.loanAmount = loanAmount;
        BigDecimal normalizedPaidAmount = this.loanRules.normalizePaidAmount(this.loanAmount, paidAmount);
        this.paidAmount = normalizedPaidAmount;

        // Clamp overpaid initial amounts; caller decides how to notify user.
        this.initialOverpaymentAdjusted = normalizedPaidAmount.compareTo(paidAmount) != 0;
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
        return loanRules.calculateRemainingBalance(loanAmount, paidAmount);
    }

    // Returns loan status based on remaining balance.
    public String getLoanStatus() {
        return loanRules.determineLoanStatus(loanAmount, paidAmount);
    }

    // Adds repayment amount to paid amount.
    public boolean makePayment(BigDecimal amount) {
        BigDecimal updatedPaidAmount = loanRules.applyRepayment(loanAmount, paidAmount, amount);
        boolean overpaymentAdjusted = updatedPaidAmount.compareTo(paidAmount.add(amount)) < 0;
        paidAmount = updatedPaidAmount;
        return overpaymentAdjusted;
    }

}
