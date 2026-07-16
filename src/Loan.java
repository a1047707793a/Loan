import java.math.BigDecimal;
import java.util.regex.Pattern;

public class Loan {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}]+(?: [\\p{L}]+)*$");

    private static int nextLoanId = 1;

    private final int loanId;
    private final String customerName;
    private final BigDecimal loanAmount;
    private BigDecimal paidAmount;

    public Loan(String customerName, BigDecimal loanAmount, BigDecimal paidAmount) {
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

        if (this.paidAmount.compareTo(this.loanAmount) > 0) {
            this.paidAmount = this.loanAmount;
            System.out.println("Excess payment has been refunded.");
        }
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getLoanId() {
        return loanId;
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
    public void makePayment(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Repayment amount must be a positive number.");
        }
        if (calculateRemainingBalance().compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("This loan is already completed.");
        }

        paidAmount = paidAmount.add(amount);
        if (paidAmount.compareTo(loanAmount) > 0) {
            paidAmount = loanAmount;
            System.out.println("Excess payment has been refunded.");
        }
    }

    private static boolean isValidName(String name) {
        return NAME_PATTERN.matcher(name).matches();
    }

    public void displayLoanInfo() {
        System.out.println("Customer ID  : " + loanId);
        System.out.println("Customer Name: " + customerName);
        System.out.println("Loan Amount  : " + loanAmount);
        System.out.println("Paid Amount  : " + paidAmount);
        System.out.println("Remaining    : " + calculateRemainingBalance());
        System.out.println("Status       : " + getLoanStatus());
    }
}
